package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.models.Expense;

public class ExpenseBaseViewModel extends ViewModel {
    protected final FirebaseQueryLiveDataElement<Expense> liveData;
    protected final String uid;

    public ExpenseBaseViewModel(String uid, String expenseId) {
        this.uid=uid;
        liveData = new FirebaseQueryLiveDataElement<>(Expense.class, FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(uid).child(expenseId));
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<QueryResult<Expense>> observer) {
        if(liveData.getValue() != null) observer.onChanged(liveData.getValue());
        liveData.observe(owner, new Observer<QueryResult<Expense>>() {
            @Override
            public void onChanged(@Nullable QueryResult<Expense> element) {
                if(element != null) observer.onChanged(element);
            }
        });
    }

    public void removeObserver(Observer<QueryResult<Expense>> observer) {
        liveData.removeObserver(observer);
    }
}
