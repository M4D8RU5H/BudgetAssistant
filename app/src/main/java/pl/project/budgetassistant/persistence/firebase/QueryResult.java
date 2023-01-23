package pl.project.budgetassistant.persistence.firebase;

import com.google.firebase.database.DatabaseError;

public class QueryResult<T> {
    private T result;
    private DatabaseError databaseError;

    public QueryResult(T result) {
        this.result = result;
    }
    public QueryResult(DatabaseError databaseError) {
        this.databaseError = databaseError;
    }

    public T getResult() {
        return result;
    }

    public boolean hasNoError() {
        return result != null;
    }
}
