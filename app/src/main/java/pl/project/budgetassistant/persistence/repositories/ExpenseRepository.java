package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataSet;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;

public class ExpenseRepository extends Repository<Expense> {
    private Query currentQuery;
    private QueryResult queryResult;

    public ExpenseRepository(LifecycleOwner lifecycleOwner, String currentUserUid) {
        super(lifecycleOwner, currentUserUid);

        currentQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(500);

        liveDataSet = new FirebaseQueryLiveDataSet<>(Expense.class, currentQuery);
        liveDataSet.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> { //Do metody observe przekazuje argument (obiekt pewnej klasy) do którego zostaną przypisane dane z bazy
            queryResult = result;
            notifyObservers();
        });
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;

        if (liveDataSet != null) {
            liveDataSet.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> { //Do metody observe przekazuje argument (obiekt pewnej klasy) do którego zostaną przypisane dane z bazy
                queryResult = result;
                notifyObservers();
            });
        } else if (liveDataElement != null) {
            liveDataElement.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> { //Do metody observe przekazuje argument (obiekt pewnej klasy) do którego zostaną przypisane dane z bazy
                queryResult = result;
                notifyObservers();
            });
        }
    }

    public Expense get(String uid) {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(uid).child(uid);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataElement();
        }

        return (Expense) queryResult.getResult();
    }

    public ListDataSet<Expense> getFirst(int count) {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(count);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataSet();
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    public ListDataSet<Expense> getFromDateRange(Calendar startDate, Calendar endDate) {
        Query newQuery = FirebaseDatabase.getInstance().getReference()
                .child("expenses").child(currentUserUid).orderByChild("timestamp")
                .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis());

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataSet();
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    private void configureLiveDataElement() {
        liveDataSet = null;

        liveDataElement = new FirebaseQueryLiveDataElement<>(Expense.class, currentQuery);

        setLifecycleOwner(lifecycleOwner);
    }

    private void configureLiveDataSet() {
        liveDataElement = null;

        if (liveDataSet == null) {
            liveDataSet = new FirebaseQueryLiveDataSet<>(Expense.class, currentQuery);
        } else {
            liveDataSet.setQuery(currentQuery);
        }

        setLifecycleOwner(lifecycleOwner);
    }
}
