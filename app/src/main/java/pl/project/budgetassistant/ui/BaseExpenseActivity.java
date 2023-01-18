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

import pl.project.budgetassistant.base.BaseActivity;
import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.firebase.models.Expense;


public abstract class BaseExpenseActivity extends BaseActivity {
    protected Spinner selectCategorySpinner;
    protected TextInputEditText selectNameEditText;
    protected Calendar chosenDate;
    protected TextInputEditText selectAmountEditText;
    protected TextView chooseDayTextView;
    protected TextView chooseTimeTextView;
    protected User user;
    protected Expense expense;
    protected Button removeEntryButton;
    protected Button editEntryButton;
    protected String expenseId;
    protected TextInputLayout selectAmountInputLayout;
    protected TextInputLayout selectNameInputLayout;

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
