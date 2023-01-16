package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserSettings {
    public static final int HOME_COUNTER_TYPE_COMPARE_INCOME_EXPENSE = 0;
    public static final int HOME_COUNTER_TYPE_SHOW_LIMIT = 1;

    public int dayOfMonthStart = 0;
    public int dayOfWeekStart = 0;
    public long limit;
    public int homeCounterType = UserSettings.HOME_COUNTER_TYPE_COMPARE_INCOME_EXPENSE;

    public UserSettings() { }

}
