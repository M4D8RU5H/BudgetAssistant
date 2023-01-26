package pl.project.budgetassistant.util;

import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.models.DefaultCategories;

public class CategoriesHelper {
    public static Category searchCategory(String categoryName) {
        DefaultCategories defaultCategories = DefaultCategories.getInstance();

        for (Category category : defaultCategories.getCategories()) {
            if (category.getCategoryID().equals(categoryName)) return category;
        }

        return defaultCategories.createDefaultCategoryModel("Others");
    }
}
