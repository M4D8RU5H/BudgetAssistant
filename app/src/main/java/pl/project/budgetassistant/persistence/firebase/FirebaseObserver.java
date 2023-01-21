package pl.project.budgetassistant.persistence.firebase;

public interface FirebaseObserver<T> {
    void onChanged(T t);
}
