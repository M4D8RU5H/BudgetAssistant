package pl.project.budgetassistant.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.base.BaseActivity;
import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.firebase.FirebaseElement;
import pl.project.budgetassistant.firebase.FirebaseObserver;
import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.firebase.models.Expense;
import pl.project.budgetassistant.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.util.CurrencyHelper;


public abstract class BaseExpenseActivity extends BaseActivity {
    protected Spinner selectCategorySpinner;
    protected TextInputEditText selectNameEditText;
    protected Calendar chosenDate;
    protected TextInputEditText selectAmountEditText;
    protected TextView chooseDayTextView;
    protected TextView chooseTimeTextView;
    protected User user;
    protected Expense expense;
    protected TextInputLayout selectAmountInputLayout;
    protected TextInputLayout selectNameInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureUI();

        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        chooseTimeTextView = findViewById(R.id.choose_time_textview);
        chooseDayTextView = findViewById(R.id.choose_day_textview);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);
        selectAmountInputLayout = findViewById(R.id.select_amount_inputlayout);
        chosenDate = Calendar.getInstance();

        updateDate();

        chooseDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        chooseTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        UserProfileViewModelFactory.getModel(getCurrentUserUid(), this).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    user = firebaseElement.getElement();
                    dateUpdated();
                }
            }
        });
    }

    abstract protected void configureUI();
    abstract protected void dateUpdated();
    abstract protected void modifyExpense(long amount, Date entryDate, String entryCategory, String entryName) throws EmptyStringException, ZeroBalanceDifferenceException;

    protected void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDayTextView.setText(dataFormatter.format(chosenDate.getTime()));

        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("HH:mm");
        chooseTimeTextView.setText(dataFormatter2.format(chosenDate.getTime()));
    }

    protected void pickTime() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                chosenDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                chosenDate.set(Calendar.MINUTE, minute);
                updateDate();

            }
        }, chosenDate.get(Calendar.HOUR_OF_DAY), chosenDate.get(Calendar.MINUTE), true).show();
    }

    protected void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        chosenDate.set(year, monthOfYear, dayOfMonth);
                        updateDate();

                    }
                }, year, month, day).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
