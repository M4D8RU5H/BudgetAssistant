package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.Query;

import java.util.Observable;

import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataSet;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;
import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.repositories.UpdateCommand;

public class ExpensesBaseViewModel extends ViewModel implements java.util.Observer {
    protected ExpenseRepository expenseRepo;
    protected UpdateCommand updateCommand;

    public ExpensesBaseViewModel(ExpenseRepository expenseRepo) {
        if (expenseRepo != null && this.expenseRepo == null) {
            this.expenseRepo = expenseRepo;
            expenseRepo.addObserver(this);
        }
    }

    public void setUpdateCommand(UpdateCommand updateCommand) {
        this.updateCommand = updateCommand;
        updateCommand.execute();
    }

    public ExpenseRepository getExpenseRepository(LifecycleOwner owner) {
        if (expenseRepo != null) {
            expenseRepo.setLifecycleOwner(owner);
        }

        return expenseRepo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (updateCommand != null) {
            updateCommand.execute();
        }
    }
}
