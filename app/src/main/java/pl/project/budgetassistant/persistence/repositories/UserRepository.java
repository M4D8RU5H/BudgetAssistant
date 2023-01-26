package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.persistence.firebase.QueryResult;

public class UserRepository extends Repository {

    public UserRepository(LifecycleOwner lifecycleOwner, String currentUserUid) {
        super(lifecycleOwner, currentUserUid);
    }

    public User getCurrentUser() {
        Query newQuery = database.child("users").child(currentUserUid);

        if (!areQueriesTheSame(currentQuery, newQuery)) {
            currentQuery = newQuery;
            configureLiveDataElement();
        }

        if (queryResult != null) {
            return (User) queryResult.getResult();
        } else {
            return null;
        }
    }

    public void update(User user) {
        currentQuery = null;

        database.child("users").child(currentUserUid).setValue(user);
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;

        if (liveDataElement != null) {
            liveDataElement.observe(lifecycleOwner, (Observer<? super QueryResult>) result -> {
                queryResult = result;
                notifyObservers();
            });
        }
    }

    private void configureLiveDataElement() {
        liveDataElement = new FirebaseQueryLiveDataElement<>(User.class, currentQuery);

        setLifecycleOwner(lifecycleOwner);
    }
}