package pl.project.budgetassistant.persistence.viewmodels;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Observable;

import pl.project.budgetassistant.persistence.firebase.FirebaseElement;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.models.Expense;

public class ExpenseBaseViewModel extends ViewModel implements Observer {
    protected final FirebaseQueryLiveDataElement<Expense> liveData;
    protected final String uid;

    public ExpenseBaseViewModel(String uid, String expenseId) {
        this.uid=uid;
        liveData = new FirebaseQueryLiveDataElement<>(Expense.class, FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(uid).child(expenseId));
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<FirebaseElement<Expense>> observer) {
        if(liveData.getValue() != null) observer.onChanged(liveData.getValue());
        liveData.observe(owner, new Observer<FirebaseElement<Expense>>() {
            @Override
            public void onChanged(@Nullable FirebaseElement<Expense> element) {
                if(element != null) observer.onChanged(element);
            }
        });
    }

    public void removeObserver(Observer<FirebaseElement<Expense>> observer) {
        liveData.removeObserver(observer);
    }

    @Override
    public void onChanged(Object o) {
        Log.d("Nadzieja", "Niech to się uda");
    }
}
