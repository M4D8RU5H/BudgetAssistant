package pl.project.budgetassistant.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.models.DefaultCategories;

public class CategoriesHelper {
    public static Category searchCategory(String categoryName) {
        for(Category category : DefaultCategories.getDefaultCategories()) {
            if(category.getCategoryID().equals(categoryName)) return category;
        }

        return DefaultCategories.createDefaultCategoryModel("Others");
    }

    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.addAll(Arrays.asList(DefaultCategories.getDefaultCategories()));
        return categories;
    }
}
