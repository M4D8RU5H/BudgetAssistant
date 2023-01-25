package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataSet;

public abstract class Repository<T> extends Observable{
    protected DatabaseReference database;
    protected FirebaseQueryLiveDataElement<T> liveDataElement;
    protected FirebaseQueryLiveDataSet<T> liveDataSet;
    protected Class<T> genericTypeClass;
    protected String childNodeName;
    protected String currentUserUid;
    protected LifecycleOwner lifecycleOwner;

    public Repository(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
        //database = FirebaseDatabase.getInstance().getReference();
        //liveData = new FirebaseQueryLiveDataElement<T>(genericTypeClass, database);
    }

    protected boolean areQueriesTheSame(Query q1, Query q2) {
        if (q1 == null || q2 == null) { return false; }

        return q1.getSpec().getParams().toString().equals(
                q2.getSpec().getParams().toString()
        );
    }

    public List<T> getAll() {
        return null;
    }

    public void add(Object entity) {
    }

    public void remove(Object entity) {
    }
}
