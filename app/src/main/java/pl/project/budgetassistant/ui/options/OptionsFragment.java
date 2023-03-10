package pl.project.budgetassistant.ui.options;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.ArrayList;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.exceptions.NumberRangeException;
import pl.project.budgetassistant.persistence.repositories.UserRepository;
import pl.project.budgetassistant.ui.viewmodels.UserProfileBaseViewModel;
import pl.project.budgetassistant.ui.signin.SignInActivity;
import pl.project.budgetassistant.ui.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.util.CurrencyHelper;

public class OptionsFragment extends PreferenceFragmentCompat {
    User user;

    UserProfileBaseViewModel userViewModel;
    UserRepository userRepo;

    ArrayList<Preference> preferences = new ArrayList<>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = UserProfileViewModelFactory.getModel(getActivity(), getCurrentUserUid());
        userRepo = userViewModel.getRepository();

        addPreferencesFromResource(R.xml.app_preferences);

        Field[] fields = R.string.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().startsWith("pref_key")) {
                try {
                    preferences.add(findPreference(getString((int) fields[i].get(null))));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Preference preference : preferences) {
            preference.setEnabled(false);
        }

        userViewModel.setUpdateCommand(() -> {
            user = userRepo.getCurrentUser();
            if (user == null) return;

            dataUpdated();
        });

        Preference logoutPreference = findPreference(getString(R.string.pref_key_logout));
        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("pl.cyfrogen.budget.ACTION_LOGOUT");
                getActivity().sendBroadcast(broadcastIntent);
                getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
                return true;
            }
        });
    }

    private void dataUpdated() {
        for (Preference preference : preferences) {
            preference.setEnabled(true);
        }

        Preference currencyPreference = findPreference(getString(R.string.pref_key_currency));
        currencyPreference.setSummary(user.currency.symbol);
        currencyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Waluta");
                View layout = getLayoutInflater().inflate(R.layout.set_currency_dialog, null);

                TextInputEditText currencyEditText = layout.findViewById(R.id.currency_edittext);
                currencyEditText.setText(user.currency.symbol);
                CheckBox showCurrencyOnLeft = layout.findViewById(R.id.show_currency_on_left_checkbox);
                showCurrencyOnLeft.setChecked(user.currency.leftSide);
                CheckBox addSpaceCheckBox = layout.findViewById(R.id.add_space_currency_checkbox);
                addSpaceCheckBox.setChecked(user.currency.hasSpace);

                alert.setView(layout);
                alert.setNegativeButton("Anuluj", null);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.currency.leftSide = showCurrencyOnLeft.isChecked();
                        user.currency.hasSpace = addSpaceCheckBox.isChecked();
                        user.currency.symbol = currencyEditText.getText().toString();
                        saveUser(user);
                    }
                });
                alert.create().show();
                return true;
            }
        });

        Preference firstMonthDayPreference = findPreference(getString(R.string.pref_key_first_month_day));
        firstMonthDayPreference.setSummary("" + (user.userSettings.dayOfMonthStart + 1));
        firstMonthDayPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Wybierz pierwszy dzie?? miesi??ca:");
                View layout = getLayoutInflater().inflate(R.layout.set_first_day_of_month_dialog, null);
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                editText.setText("" + (user.userSettings.dayOfMonthStart + 1));
                alert.setView(layout);
                alert.setNegativeButton("Anuluj", null);
                alert.setPositiveButton("OK", null);
                AlertDialog alertDialog = alert.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                try {
                                    setDate(editText.getText().toString());
                                } catch (NumberRangeException e) {
                                    editText.setError(e.getMessage());
                                }

                            }

                            private void setDate(String s) throws NumberRangeException {
                                int number = Integer.parseInt(s);
                                if (number <= 0 || number >= 29) {
                                    throw new NumberRangeException("Liczba musi by?? z zakresu 1-29");
                                } else {
                                    user.userSettings.dayOfMonthStart = number - 1;
                                    saveUser(user);
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
                return true;
            }
        });

        Preference limitPreference = findPreference(getString(R.string.pref_key_current_month_budget));
        limitPreference.setSummary(CurrencyHelper.formatCurrency(user.currency, user.budget.amountToSpend));
        limitPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Ustaw limit:");
                View layout = getLayoutInflater().inflate(R.layout.set_budget_dialog, null);
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                CurrencyHelper.setupAmountEditText(editText, user);
                alert.setView(layout);
                alert.setNegativeButton("Anuluj", null);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.budget.amountToSpend = CurrencyHelper.convertAmountStringToLong(editText.getText().toString());
                        saveUser(user);
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
    }

    private String getDayString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0:
                return "Poniedzia??ek";
            case 1:
                return "Wtorek";
            case 2:
                return "??roda";
            case 3:
                return "Czwartek";
            case 4:
                return "Pi??tek";
            case 5:
                return "Sobota";
            case 6:
                return "Niedziela";
        }
        return "";
    }

    private void saveUser(User user) {
        userRepo.update(user);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public String getCurrentUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}


