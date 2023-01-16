package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserSettings {
    public int dayOfMonthStart = 0;
    public int dayOfWeekStart = 0;
    public long limit;

    public UserSettings() { }

}
