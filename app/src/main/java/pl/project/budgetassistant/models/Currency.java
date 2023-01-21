package pl.project.budgetassistant.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Currency {
    public String symbol;
    public boolean leftSide;
    public boolean hasSpace;

    public Currency() { }

    public Currency(String symbol, boolean leftSide, boolean hasSpace) {
        this.symbol = symbol;
        this.leftSide = leftSide;
        this.hasSpace = hasSpace;
    }
}
