package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Expense {

    public String categoryId;
    public String name;
    public long timestamp;
    public long amount;
    public Expense() { }

    public Expense(String categoryId, String name, long timestamp, long balanceDifference) {
        this.categoryId = categoryId;
        this.name = name;
        this.timestamp = -timestamp;
        this.amount = balanceDifference;
    }

}