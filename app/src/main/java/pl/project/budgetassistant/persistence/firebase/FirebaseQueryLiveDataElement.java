package pl.project.budgetassistant.persistence.firebase;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveDataElement<T> extends LiveData<QueryResult<T>> {
    private Query query;
    private ValueEventListener listener;

    public FirebaseQueryLiveDataElement(Class<T> genericTypeClass, Query query) {
        setValue(null);
        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                T item = dataSnapshot.getValue(genericTypeClass);
                setValue(new QueryResult<>(item));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setValue(new QueryResult<>(databaseError));
                removeListener();
                setListener();
            }
        };

        this.query = query;
    }

    public void setQuery(Query query) {
        removeListener();
        this.query = query;
        setListener();
    }

    private void removeListener() {
        query.removeEventListener(listener);
    }

    private void setListener() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onActive() {
        setListener();
    }

    @Override
    protected void onInactive() {
        removeListener();
    }
}