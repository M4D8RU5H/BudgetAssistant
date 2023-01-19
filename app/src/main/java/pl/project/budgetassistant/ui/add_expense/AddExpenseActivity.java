package pl.project.budgetassistant.ui.add_expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.project.budgetassistant.activities.CircularRevealActivity;
import pl.project.budgetassistant.base.BaseActivity;
import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.firebase.FirebaseElement;
import pl.project.budgetassistant.firebase.FirebaseObserver;
import pl.project.budgetassistant.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.models.DefaultCategories;
import pl.project.budgetassistant.ui.BaseExpenseActivity;
import pl.project.budgetassistant.util.CategoriesHelper;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.util.CurrencyHelper;
import pl.project.budgetassistant.R;
import pl.project.budgetassistant.firebase.models.Expense;

public class AddExpenseActivity extends BaseExpenseActivity {
    private Button addEntryButton;

    @Override
    protected void configureUI() {
        setContentView(R.layout.activity_add_epense);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dodaj wydatek");

        addEntryButton = findViewById(R.id.add_entry_button);

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modifyExpense(-CurrencyHelper.convertAmountStringToLong(selectAmountEditText.getText().toString()),
                            chosenDate.getTime(),
                            ((Category) selectCategorySpinner.getSelectedItem()).getCategoryID(),
                            selectNameEditText.getText().toString());
                } catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                } catch (ZeroBalanceDifferenceException e) {
                    selectAmountInputLayout.setError(e.getMessage());
                }
            }
        });
    }

    protected void dateUpdated() {
        if (user == null) return;

        final List<Category> categories = Arrays.asList(DefaultCategories.getInstance().getDefaultCategories());
        ExpenseCategoriesAdapter categoryAdapter = new ExpenseCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);

        CurrencyHelper.setupAmountEditText(selectAmountEditText, user);
    }

    protected void modifyExpense(long amount, Date entryDate, String entryCategory, String entryName) throws ZeroBalanceDifferenceException, EmptyStringException {
        if (amount == 0) {
            throw new ZeroBalanceDifferenceException("Różnica środków nie powinna wynosić 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Nazwa wpisu nie może być pusta");
        }

        FirebaseDatabase.getInstance().getReference().child("expenses").child(getCurrentUserUid())
                .push().setValue(new Expense(entryCategory, entryName, entryDate.getTime(), amount));

        user.budget.spentAmount += amount;
        UserProfileViewModelFactory.saveModel(getCurrentUserUid(), user);

        finish();
    }
}
