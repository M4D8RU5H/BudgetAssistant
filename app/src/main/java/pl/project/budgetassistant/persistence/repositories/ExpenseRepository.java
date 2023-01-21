package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.database.FirebaseDatabase;

import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.firebase.FirebaseElement;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;

public class ExpenseRepository extends Repository<Expense> {
    public ExpenseRepository(LifecycleOwner owner, String currentUserUid) {
        super(owner, currentUserUid);

        childNodeName = "expenses";

        liveData = new FirebaseQueryLiveDataElement<>(Expense.class, FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(500));
        liveData.observe(owner, (Observer<? super FirebaseElement<Expense>>) element -> {
            notifyObservers();
        });
    }

    public Expense get(String uid) {
        //return (Expense) database.child(childNodeName).child(currentUserUid).child(uid);
        return null;
    }
}
