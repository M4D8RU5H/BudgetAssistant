package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Budget {
    public long amountToSpend;
    public BudgetAnalyzer analyzer = new BudgetAnalyzer();

    public Budget() { }

}
