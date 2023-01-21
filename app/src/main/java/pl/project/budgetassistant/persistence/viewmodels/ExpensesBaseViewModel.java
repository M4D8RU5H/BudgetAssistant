package pl.project.budgetassistant.persistence.viewmodels;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.Query;

import java.util.Observable;

import pl.project.budgetassistant.persistence.firebase.FirebaseElement;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataSet;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;
import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;

public class ExpensesBaseViewModel extends ViewModel implements java.util.Observer {
    protected final FirebaseQueryLiveDataSet<Expense> liveData;
    protected final String uid;
    protected final ExpenseRepository expenseRepo;

    public ExpensesBaseViewModel(String uid, Query query, ExpenseRepository expenseRepo) {
        this.uid = uid;
        this.expenseRepo = expenseRepo;

        if (expenseRepo != null) {
            expenseRepo.addObserver(this);
        }

        liveData = new FirebaseQueryLiveDataSet<>(Expense.class, query);
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<FirebaseElement<ListDataSet<Expense>>> observer) {
        observer.onChanged(liveData.getValue());
        liveData.observe(owner, new Observer<FirebaseElement<ListDataSet<Expense>>>() {
            @Override
            public void onChanged(@Nullable FirebaseElement<ListDataSet<Expense>> element) {
                if(element != null) observer.onChanged(element);
            }
        });
    }

    public void removeObserver(Observer<FirebaseElement<ListDataSet<Expense>>> observer) {
        liveData.removeObserver(observer);
    }


    @Override
    public void update(Observable o, Object arg) {
        Log.d("fdsf", "fdsf");
    }
}
