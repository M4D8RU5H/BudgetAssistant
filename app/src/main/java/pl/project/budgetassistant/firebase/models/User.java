package pl.project.budgetassistant.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public Currency currency = new Currency("zł", false, true);
    public UserSettings userSettings = new UserSettings();
    public Wallet wallet = new Wallet();

    public User() { }
}