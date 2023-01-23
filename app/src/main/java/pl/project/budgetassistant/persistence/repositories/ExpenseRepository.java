package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataSet;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;

public class ExpenseRepository extends Repository<Expense> {
    private Query currentQuery;
    private QueryResult queryResult;

    public ExpenseRepository(LifecycleOwner owner, String currentUserUid) {
        super(owner, currentUserUid);

        childNodeName = "expenses";
        currentQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(500);
        liveDataSet = new FirebaseQueryLiveDataSet<>(Expense.class, currentQuery);

        liveDataSet.observe(owner, (Observer<? super QueryResult>) result -> { //Do metody observe przekazuje argument (obiekt pewnej klasy) do którego zostaną przypisane dane z bazy
            queryResult = result;
            notifyObservers();
        });
    }

    public Expense get(String uid) {
        //return (Expense) database.child(childNodeName).child(currentUserUid).child(uid);
        return null;
    }

    public ListDataSet<Expense> GetAll() {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp");

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            liveDataSet.setQuery(newQuery);
            currentQuery = newQuery;
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    public ListDataSet<Expense> getFirst(int count) {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(count);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            liveDataSet.setQuery(newQuery);
            currentQuery = newQuery;
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    public ListDataSet<Expense> getFromDateRange(Calendar startDate, Calendar endDate) {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp")
                .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis());

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            liveDataSet.setQuery(newQuery);
            currentQuery = newQuery;
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }
}
