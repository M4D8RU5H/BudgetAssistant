package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Expense {

    public String categoryID;
    public String name;
    public long timestamp;
    public long amount;
    public Expense() { }

    public Expense(String categoryID, String name, long timestamp, long balanceDifference) {
        this.categoryID = categoryID;
        this.name = name;
        this.timestamp = -timestamp;
        this.amount = balanceDifference;
    }

}