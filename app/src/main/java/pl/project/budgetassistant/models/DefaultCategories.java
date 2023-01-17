package pl.project.budgetassistant.models;

import android.graphics.Color;

import pl.project.budgetassistant.R;

public class DefaultCategories {
    private static DefaultCategories instance;
    private Category[] categories;

    private DefaultCategories() {
        categories = new Category[]{
                new Category(":others", "Inne", R.drawable.category_default, Color.parseColor("#455a64")),
                new Category(":clothing", "Odzież", R.drawable.category_clothing, Color.parseColor("#d32f2f")),
                new Category(":food", "Jedzenie", R.drawable.category_food, Color.parseColor("#c2185b")),
                new Category(":gas_station", "Paliwo", R.drawable.category_gas_station, Color.parseColor("#7b1fa2")),
                new Category(":gaming", "Rozrywka", R.drawable.category_gaming, Color.parseColor("#512da8")),
                new Category(":gift", "Prezenty", R.drawable.category_gift, Color.parseColor("#303f9f")),
                new Category(":holidays", "Wakacje", R.drawable.category_holidays, Color.parseColor("#1976d2")),
                new Category(":home", "Dom", R.drawable.category_home, Color.parseColor("#0288d1")),
                new Category(":kids", "Dzieci", R.drawable.category_kids, Color.parseColor("#0097a7")),
                new Category(":pharmacy", "Leki", R.drawable.category_pharmacy, Color.parseColor("#00796b")),
                new Category(":repair", "Naprawa", R.drawable.category_repair, Color.parseColor("#388e3c")),
                new Category(":shopping", "Zakupy", R.drawable.category_shopping, Color.parseColor("#689f38")),
                new Category(":sport", "Sport", R.drawable.category_sport, Color.parseColor("#afb42b")),
                new Category(":transfer", "Przelewy", R.drawable.category_transfer, Color.parseColor("#fbc02d")),
                new Category(":transport", "Transport", R.drawable.category_transport, Color.parseColor("#ffa000")),
                new Category(":work", "Praca", R.drawable.category_briefcase, Color.parseColor("#f57c00")),
        };
    }

    public static DefaultCategories getInstance() {
        if (instance == null) { instance = new DefaultCategories(); }
        return instance;
    }

    public static Category createDefaultCategoryModel(String visibleName) {
        return new Category("default", visibleName, R.drawable.category_default,
                Color.parseColor("#26a69a"));
    }

    public Category[] getDefaultCategories() {
        return categories;
    }
}
