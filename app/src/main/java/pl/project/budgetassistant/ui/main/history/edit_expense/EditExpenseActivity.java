package pl.project.budgetassistant.ui.main.history.edit_expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
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

import pl.project.budgetassistant.base.BaseActivity;
import pl.project.budgetassistant.exceptions.EmptyStringException;
import pl.project.budgetassistant.exceptions.ZeroBalanceDifferenceException;
import pl.project.budgetassistant.firebase.FirebaseElement;
import pl.project.budgetassistant.firebase.FirebaseObserver;
import pl.project.budgetassistant.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.firebase.viewmodel_factories.ExpenseViewModelFactory;
import pl.project.budgetassistant.models.DefaultCategories;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.ui.add_expense.ExpenseCategoriesAdapter;
import pl.project.budgetassistant.util.CurrencyHelper;
import pl.project.budgetassistant.R;
import pl.project.budgetassistant.firebase.models.Expense;

public class EditExpenseActivity extends BaseActivity {

    private Spinner selectCategorySpinner;
    private TextInputEditText selectNameEditText;
    private Calendar choosedDate;
    private TextInputEditText selectAmountEditText;
    private TextView chooseDayTextView;
    private TextView chooseTimeTextView;
    private Spinner selectTypeSpinner;
    private User user;
    private Expense expense;
    private Button removeEntryButton;
    private Button editEntryButton;
    private String expenseId;
    private TextInputLayout selectAmountInputLayout;
    private TextInputLayout selectNameInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit expense");

        expenseId = getIntent().getExtras().getString("expense-id");

        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        editEntryButton = findViewById(R.id.edit_entry_button);
        removeEntryButton = findViewById(R.id.remove_entry_button);
        chooseTimeTextView = findViewById(R.id.choose_time_textview);
        chooseDayTextView = findViewById(R.id.choose_day_textview);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);
        selectAmountInputLayout = findViewById(R.id.select_amount_inputlayout);

        choosedDate = Calendar.getInstance();

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


        editEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editExpense(-CurrencyHelper.convertAmountStringToLong(selectAmountEditText.getText().toString()),
                            choosedDate.getTime(),
                            ((Category) selectCategorySpinner.getSelectedItem()).getCategoryID(),
                            selectNameEditText.getText().toString());
                }  catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                } catch (ZeroBalanceDifferenceException e) {
                    selectAmountInputLayout.setError(e.getMessage());
                }
            }
        });

        removeEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveExpenseDialog();
            }

            public void showRemoveExpenseDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditExpenseActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeExpense();
                    }
                }).setNegativeButton("No", null).show();
            }
        });


        UserProfileViewModelFactory.getModel(getCurrentUserUid(), this).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    user = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });


        ExpenseViewModelFactory.getModel(getCurrentUserUid(), expenseId, this).observe(this, new FirebaseObserver<FirebaseElement<Expense>>() {
            @Override
            public void onChanged(FirebaseElement<Expense> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    expense = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });
    }

    public void dataUpdated() {
        if (expense == null || user == null) return;

        final List<Category> categories = Arrays.asList(DefaultCategories.getInstance().getDefaultCategories());
        ExpenseCategoriesAdapter categoryAdapter = new ExpenseCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);

        CurrencyHelper.setupAmountEditText(selectAmountEditText, user);
        choosedDate.setTimeInMillis(-expense.timestamp);
        updateDate();
        selectNameEditText.setText(expense.name);

        selectCategorySpinner.post(new Runnable() {
            @Override
            public void run() {
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

    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDayTextView.setText(dataFormatter.format(choosedDate.getTime()));

        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("HH:mm");
        chooseTimeTextView.setText(dataFormatter2.format(choosedDate.getTime()));
    }

    public void editExpense(long amount, Date entryDate, String entryCategory, String entryName) throws EmptyStringException, ZeroBalanceDifferenceException {
        if (amount == 0) {
            throw new ZeroBalanceDifferenceException("Balance difference should not be 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }

        long finalBalanceDifference = amount - expense.amount;
        user.budget.spentAmount += finalBalanceDifference;
        UserProfileViewModelFactory.saveModel(getCurrentUserUid(), user);

        FirebaseDatabase.getInstance().getReference().child("expenses").child(getCurrentUserUid())
                .child(expenseId).setValue(new Expense(entryCategory, entryName, entryDate.getTime(), amount));
        finish();
    }

    public void removeExpense() {
        user.budget.spentAmount -= expense.amount;
        UserProfileViewModelFactory.saveModel(getCurrentUserUid(), user);

        FirebaseDatabase.getInstance().getReference().child("expenses").child(getCurrentUserUid())
                .child(expenseId).removeValue();
        finish();
    }


    private void pickTime() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                choosedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                choosedDate.set(Calendar.MINUTE, minute);
                updateDate();

            }
        }, choosedDate.get(Calendar.HOUR_OF_DAY), choosedDate.get(Calendar.MINUTE), true).show();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        choosedDate.set(year, monthOfYear, dayOfMonth);
                        updateDate();

                    }
                }, year, month, day).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }

}
