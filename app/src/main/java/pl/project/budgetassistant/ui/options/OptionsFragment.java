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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.ArrayList;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.exceptions.NumberRangeException;
import pl.project.budgetassistant.ui.signin.SignInActivity;
import pl.project.budgetassistant.firebase.FirebaseElement;
import pl.project.budgetassistant.firebase.FirebaseObserver;
import pl.project.budgetassistant.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.util.CurrencyHelper;

public class OptionsFragment extends PreferenceFragmentCompat {
    User user;
    ArrayList<Preference> preferences = new ArrayList<>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {

            @Override
            public void onChanged(FirebaseElement<User> element) {
                if (!element.hasNoError()) return;
                OptionsFragment.this.user = element.getElement();
                dataUpdated();
            }
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
                alert.setTitle("Wybierz pierwszy dzień miesiąca:");
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
                                    throw new NumberRangeException("Liczba musi być z zakresu 1-29");
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
                return "Poniedziałek";
            case 1:
                return "Wtorek";
            case 2:
                return "Środa";
            case 3:
                return "Czwartek";
            case 4:
                return "Piątek";
            case 5:
                return "Sobota";
            case 6:
                return "Niedziela";
        }
        return "";
    }

    private void saveUser(User user) {
        UserProfileViewModelFactory.saveModel(getUid(), user);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}


