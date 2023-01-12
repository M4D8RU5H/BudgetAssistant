package pl.project.budgetassistant.firebase;

public interface FirebaseObserver<T> {
    void onChanged(T t);
}
