package pl.project.budgetassistant.ui.main.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.repositories.UserRepository;
import pl.project.budgetassistant.ui.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.ui.viewmodel_factories.ExpensesHistoryViewModelFactory;
import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.ui.viewmodels.UserProfileBaseViewModel;
import pl.project.budgetassistant.util.CategoriesHelper;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.ui.main.history.edit_expense.EditExpenseActivity;
import pl.project.budgetassistant.util.CurrencyHelper;

public class ExpensesRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseHolder> {

    private final String currentUserUid;
    private final FragmentActivity fragmentActivity;
    private ListDataSet<Expense> expenses;

    private User user;
    private boolean firstUserSync = false;
    private ExpenseRepository expenseRepo;
    private ExpensesHistoryViewModelFactory.Model expensesHistoryViewModel;
    private UserProfileBaseViewModel userViewModel;
    private UserRepository userRepo;

    public ExpensesRecyclerViewAdapter(FragmentActivity fragmentActivity, String currentUserUid) {
        this.fragmentActivity = fragmentActivity;
        this.currentUserUid = currentUserUid;

        expensesHistoryViewModel = ExpensesHistoryViewModelFactory.getModel(fragmentActivity, currentUserUid);
        expenseRepo = expensesHistoryViewModel.getRepository();

        userViewModel = UserProfileViewModelFactory.getModel(fragmentActivity, currentUserUid);
        userRepo = userViewModel.getRepository();

        userViewModel.setUpdateCommand(() -> {
            user = userRepo.getCurrentUser();
            if (user == null) return;

            if(!firstUserSync) {
                expensesHistoryViewModel.setUpdateCommand(() -> {
                    expenses = expenseRepo.getFirst(500);
                    expenses.notifyRecycler(ExpensesRecyclerViewAdapter.this);
                    notifyDataSetChanged();
                });
            }

            firstUserSync = true;
        });
    }

    @Override
    public ExpenseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragmentActivity);
        View view = inflater.inflate(R.layout.history_listview_row, parent, false);
        return new ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseHolder holder, int position) {
        String expenseUid = expenses.getIDList().get(position);
        Expense expense = expenses.getList().get(position);
        Category category = CategoriesHelper.searchCategory(expense.categoryId);
        holder.iconImageView.setImageResource(category.getIconResourceID());
        holder.iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));
        holder.categoryTextView.setText(category.getCategoryVisibleName(fragmentActivity));
        holder.nameTextView.setText(expense.name);

        Date date = new Date(-expense.timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        holder.dateTextView.setText(dateFormat.format(date));
        holder.moneyTextView.setText(CurrencyHelper.formatCurrency(user.currency, expense.amount));
        holder.moneyTextView.setTextColor(ContextCompat.getColor(fragmentActivity,
                expense.amount < 0 ? R.color.primary_text_expense : R.color.primary_text_income));

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDeleteDialog(expenseUid, expense.amount, fragmentActivity);
                return false;
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragmentActivity, EditExpenseActivity.class);
                intent.putExtra("expense-uid", expenseUid);
                fragmentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (expenses == null) return 0;
        return expenses.getList().size();
    }

    private void createDeleteDialog(String id, long balanceDifference, Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Czy chcesz usun?????")
                .setPositiveButton("Usu??", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        expenseRepo.remove(id);

                        user =userRepo.getCurrentUser();

                        user.budget.spentAmount -= balanceDifference;
                        userRepo.update(user);

                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create().show();
    }

    public void setDateRange(Calendar startDate, Calendar endDate) {
        expensesHistoryViewModel.setUpdateCommand(() -> {
            expenses = expenseRepo.getFromDateRange(startDate, endDate);
            expenses.notifyRecycler(ExpensesRecyclerViewAdapter.this);
        });

        expensesHistoryViewModel.setDateRange(startDate, endDate);
    }
}