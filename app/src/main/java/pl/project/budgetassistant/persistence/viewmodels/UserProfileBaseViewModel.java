package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.models.User;

public class UserProfileBaseViewModel extends ViewModel {
    private final FirebaseQueryLiveDataElement<User> liveData;

    public UserProfileBaseViewModel(String uid) {
        liveData = new FirebaseQueryLiveDataElement<>(User.class,
                FirebaseDatabase.getInstance().getReference().child("users").child(uid));
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<QueryResult<User>> observer) {
        if (liveData.getValue() != null) { observer.onChanged(liveData.getValue()); }

        liveData.observe(owner, new Observer<QueryResult<User>>() {
            @Override
            public void onChanged(@Nullable QueryResult<User> queryResult) {
                if(queryResult != null) observer.onChanged(queryResult);
            }
        });
    }
}