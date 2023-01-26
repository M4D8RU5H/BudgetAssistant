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

public class ExpenseRepository extends Repository {

    public ExpenseRepository(LifecycleOwner lifecycleOwner, String currentUserUid) {
        super(lifecycleOwner, currentUserUid);
    }

    public Expense get(String expenseUid) {
        Query newQuery = database.child("expenses").child(currentUserUid).child(expenseUid);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataElement();
        }

        if (queryResult != null) {
            return (Expense) queryResult.getResult();
        } else {
            return null;
        }
    }

    public ListDataSet<Expense> getFirst(int count) {
        Query newQuery = database.child("expenses").child(currentUserUid).orderByChild("timestamp").limitToFirst(count);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataSet();
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    public ListDataSet<Expense> getFromDateRange(Calendar startDate, Calendar endDate) {
        Query newQuery = database.child("expenses").child(currentUserUid).orderByChild("timestamp")
                .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis());

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataSet();
        }

        return (ListDataSet<Expense>) queryResult.getResult();
    }

    public void add(Expense expense) {
        currentQuery = null;
        database.child("expenses").child(currentUserUid).push().setValue(expense);
    }

    public void update(String expenseUid, Expense expense) {
        currentQuery = null;

        database.child("expenses").child(currentUserUid).child(expenseUid).setValue(expense);
    }

    public void remove(String expenseUid) {
        currentQuery = null;

        database.child("expenses").child(currentUserUid).child(expenseUid).removeValue();
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;

        if (liveDataSet != null) {
            liveDataSet.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> {
                queryResult = result;
                notifyObservers();
            });
        } else if (liveDataElement != null) {
            liveDataElement.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> {
                queryResult = result;
                notifyObservers();
            });
        }
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