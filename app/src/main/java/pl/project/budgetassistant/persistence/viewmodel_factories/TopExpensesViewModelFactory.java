package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.viewmodels.ExpensesBaseViewModel;

public class TopExpensesViewModelFactory implements ViewModelProvider.Factory {
    private ExpenseRepository expenseRepo;

    TopExpensesViewModelFactory(ExpenseRepository expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(expenseRepo);
    }

    public static Model getModel(FragmentActivity activity, ExpenseRepository expenseRepo) {
        Model model = ViewModelProviders.of(activity, new TopExpensesViewModelFactory(expenseRepo)).get(Model.class);
        model.update(null, null);
        return model;
    }

    public static class Model extends ExpensesBaseViewModel {
        public Model(ExpenseRepository expenseRepo) {
            super(expenseRepo);
        }
    }
}