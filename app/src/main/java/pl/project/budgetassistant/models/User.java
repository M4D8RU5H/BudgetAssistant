package pl.project.budgetassistant.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public Currency currency = new Currency("zł", false, true);
    public UserSettings userSettings = new UserSettings();
    public Budget budget = new Budget();

    //TODO Zmienić modifikatory dostępu do pól modeli na prywatne, stworzyc odpowiednie gettery i settery

    public User() { }
}