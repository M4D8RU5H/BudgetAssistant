package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.viewmodels.ExpensesBaseViewModel;

public class ExpensesHistoryViewModelFactory implements ViewModelProvider.Factory {
    private ExpenseRepository expenseRepo;

    ExpensesHistoryViewModelFactory(ExpenseRepository expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(expenseRepo);
    }

    public static Model getModel(FragmentActivity activity, ExpenseRepository expenseRepo) {
        return ViewModelProviders.of(activity, new ExpensesHistoryViewModelFactory(expenseRepo)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {
        private Calendar endDate;
        private Calendar startDate;

        public Model(ExpenseRepository expenseRepo) {
            super(expenseRepo);
        }

        public void setDateRange(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public boolean hasDateSet() {
            return startDate != null && endDate != null;
        }

        public Calendar getStartDate() {
            return startDate;
        }

        public Calendar getEndDate() {
            return endDate;
        }
    }
}