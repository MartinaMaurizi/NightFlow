package it.ispwproject.nightflow.pattern.payment;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {

    protected final List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer observer){
        observers.add(observer);
    }

    public synchronized void notifyObservers(){
        for (Observer observer : observers) {
            observer.update();
        }
    }
}