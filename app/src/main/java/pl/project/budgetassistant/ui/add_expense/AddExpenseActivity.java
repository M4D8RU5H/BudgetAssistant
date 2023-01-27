package pl.project.budgetassistant.ui.add_expense;

import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.models.DefaultCategories;
import pl.project.budgetassistant.ui.BaseExpenseActivity;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.util.CurrencyHelper;
import pl.project.budgetassistant.R;
import pl.project.budgetassistant.models.Expense;

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

    @Override
    protected void dateUpdated() {
        if (user == null) return;

        final List<Category> categories = Arrays.asList(DefaultCategories.getInstance().getCategories());
        ExpenseCategoriesAdapter categoryAdapter = new ExpenseCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);

        CurrencyHelper.setupAmountEditText(selectAmountEditText, user);
    }

    @Override
    protected void modifyExpense(long amount, Date entryDate, String entryCategory, String entryName) throws ZeroBalanceDifferenceException, EmptyStringException {
        if (amount == 0) {
            throw new ZeroBalanceDifferenceException("Różnica środków nie powinna wynosić 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Nazwa wpisu nie może być pusta");
        }

        expenseRepo.add(new Expense(entryCategory, entryName, entryDate.getTime(), amount));

        user.budget.spentAmount += amount;
        userRepo.update(user);

        finish();
    }
}
