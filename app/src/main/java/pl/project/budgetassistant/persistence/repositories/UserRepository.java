package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.persistence.firebase.QueryResult;

public class UserRepository extends Repository<User> {
    private Query currentQuery;
    private QueryResult queryResult;

    public UserRepository(LifecycleOwner lifecycleOwner, String currentUserUid) {
        super(lifecycleOwner, currentUserUid);
    }

    public User getCurrentUser() {
        Query newQuery = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid);

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

    public void add(User user) {
        currentQuery = null;

        //
    }

    public void update(User user) {
        currentQuery = null;

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(currentUserUid).setValue(user);
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