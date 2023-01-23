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
    private String currentUserUid;

    ExpensesHistoryViewModelFactory(String currentUserUid, ExpenseRepository expenseRepo) {
        this.currentUserUid = currentUserUid;
        this.expenseRepo = expenseRepo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(currentUserUid, expenseRepo);
    }

    public static Model getModel(String currentUserUid, FragmentActivity activity, ExpenseRepository expenseRepo) {
        return ViewModelProviders.of(activity, new ExpensesHistoryViewModelFactory(currentUserUid, expenseRepo)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {

        private Calendar endDate;
        private Calendar startDate;

        public Model(String currentUserUid, ExpenseRepository expenseRepo) {
            super(currentUserUid, getDefaultQuery(currentUserUid), expenseRepo);
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