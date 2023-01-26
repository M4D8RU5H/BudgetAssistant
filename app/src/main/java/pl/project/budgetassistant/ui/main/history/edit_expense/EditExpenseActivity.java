package pl.project.budgetassistant.ui.main.history.edit_expense;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.repositories.UpdateCommand;
import pl.project.budgetassistant.persistence.repositories.UserRepository;
import pl.project.budgetassistant.persistence.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.persistence.viewmodel_factories.ExpenseViewModelFactory;
import pl.project.budgetassistant.models.DefaultCategories;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.persistence.viewmodels.ExpenseBaseViewModel;
import pl.project.budgetassistant.persistence.viewmodels.UserProfileBaseViewModel;
import pl.project.budgetassistant.ui.BaseExpenseActivity;
import pl.project.budgetassistant.ui.add_expense.ExpenseCategoriesAdapter;
import pl.project.budgetassistant.util.CurrencyHelper;
import pl.project.budgetassistant.R;
import pl.project.budgetassistant.models.Expense;

public class EditExpenseActivity extends BaseExpenseActivity {
    private Expense expense;
    private String expenseUid;
    private Button editExpenseButton;

    @Override
    protected void configureUI() {
        setContentView(R.layout.activity_edit_expense);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edytuj wydatek");

        editExpenseButton = findViewById(R.id.edit_entry_button);

        editExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modifyExpense(-CurrencyHelper.convertAmountStringToLong(selectAmountEditText.getText().toString()),
                            chosenDate.getTime(),
                            ((Category) selectCategorySpinner.getSelectedItem()).getCategoryID(),
                            selectNameEditText.getText().toString());
                }  catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                } catch (ZeroBalanceDifferenceException e) {
                    selectAmountInputLayout.setError(e.getMessage());
                }
            }
        });

        expenseUid = getIntent().getExtras().getString("expense-uid");

        expenseViewModel.setUpdateCommand(() -> {
            expense = expenseRepo.get(expenseUid);
            dateUpdated();
        });
    }

    protected void dateUpdated() {
        if (expense == null || user == null) return;

        final List<Category> categories = Arrays.asList(DefaultCategories.getInstance().getDefaultCategories());
        ExpenseCategoriesAdapter categoryAdapter = new ExpenseCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);

        CurrencyHelper.setupAmountEditText(selectAmountEditText, user);
        chosenDate.setTimeInMillis(-expense.timestamp);
        updateDate();
        selectNameEditText.setText(expense.name);

        selectCategorySpinner.post(new Runnable() {
            @Override
            public void run() {
                if (expense == null) return;
                ExpenseCategoriesAdapter adapter = (ExpenseCategoriesAdapter) selectCategorySpinner.getAdapter();
                selectCategorySpinner.setSelection(adapter.getItemIndex(expense.categoryId));
            }
        });

        long amount = Math.abs(expense.amount);
        String current = CurrencyHelper.formatCurrency(user.currency, amount);
        selectAmountEditText.setText(current);
        selectAmountEditText.setSelection(current.length() -
                (user.currency.leftSide ? 0 : (user.currency.symbol.length() + (user.currency.hasSpace ? 1 : 0))));

    }


    protected void modifyExpense(long amount, Date entryDate, String entryCategory, String entryName) throws EmptyStringException, ZeroBalanceDifferenceException {

        if (amount == 0) {
            throw new ZeroBalanceDifferenceException("Różnica środków nie powinna wynosić 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Nazwa nie może być pusta");
        }

        long finalBalanceDifference = amount - expense.amount;

        expenseRepo.update(expenseUid, new Expense(entryCategory, entryName, entryDate.getTime(), amount));

        user.budget.spentAmount += finalBalanceDifference;
        userRepo.update(user);

        finish();
    }

    private void removeExpense() {
        user.budget.spentAmount -= expense.amount;
        userRepo.update(user);

        expenseRepo.remove(expenseUid);

        finish();
    }
}
