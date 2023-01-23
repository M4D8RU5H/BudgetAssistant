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
    protected final FirebaseQueryLiveDataSet<Expense> liveData;
    protected final String uid;
    protected ExpenseRepository expenseRepo;
    protected UpdateCommand updateCommand;

    public ExpensesBaseViewModel(String uid, Query query, ExpenseRepository expenseRepo) {
        this.uid = uid;
        this.expenseRepo = expenseRepo;

        if (expenseRepo != null) {
            expenseRepo.addObserver(this);
        }

        liveData = new FirebaseQueryLiveDataSet<>(Expense.class, query);
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<QueryResult<ListDataSet<Expense>>> observer) {
        observer.onChanged(liveData.getValue());
        liveData.observe(owner, new Observer<QueryResult<ListDataSet<Expense>>>() {
            @Override
            public void onChanged(@Nullable QueryResult<ListDataSet<Expense>> element) {
                if(element != null) observer.onChanged(element);
            }
        });
    }

    public void setUpdateCommand(UpdateCommand updateCommand) {
        this.updateCommand = updateCommand;
        updateCommand.execute();
    }

    @Override
    public void update(Observable o, Object arg) {
        updateCommand.execute();
    }
}
