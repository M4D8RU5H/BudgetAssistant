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
    private String uid;
    private ExpenseRepository expenseRepo;

    ExpensesHistoryViewModelFactory(String uid, ExpenseRepository expenseRepo) {
        this.uid = uid;
        this.expenseRepo = expenseRepo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid, expenseRepo);
    }

    public static Model getModel(String uid, FragmentActivity activity, ExpenseRepository expenseRepo) {
        return ViewModelProviders.of(activity, new ExpensesHistoryViewModelFactory(uid, expenseRepo)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {

        private Calendar endDate;
        private Calendar startDate;

        public Model(String uid, ExpenseRepository expenseRepo) {
            super(uid, getDefaultQuery(uid), expenseRepo);
        }

        private static Query getDefaultQuery(String uid) {
            return FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(uid).orderByChild("timestamp").limitToFirst(500);
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