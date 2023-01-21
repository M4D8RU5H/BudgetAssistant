package pl.project.budgetassistant.persistence.repositories;

import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;

public abstract class Repository<T> extends Observable{
    protected DatabaseReference database;
    protected FirebaseQueryLiveDataElement<T> liveData;
    protected Class<T> genericTypeClass;
    protected String childNodeName;
    protected String currentUserUid;
    protected LifecycleOwner owner;

    public Repository(LifecycleOwner owner, String currentUserUid) {
        this.owner = owner;
        this.currentUserUid = currentUserUid;
        //database = FirebaseDatabase.getInstance().getReference();
        //liveData = new FirebaseQueryLiveDataElement<T>(genericTypeClass, database);
    }

    public List<T> getAll() {
        return null;
    }

    public void add(Object entity) {

    }

    public void remove(Object entity) {

    }
}
