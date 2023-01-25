package pl.project.budgetassistant.persistence.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class Observable {
    private List<Observer> observers;

    public Observable() {
        observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for(Observer observer : observers) {
            observer.update(null, null);
        }
    }
}
