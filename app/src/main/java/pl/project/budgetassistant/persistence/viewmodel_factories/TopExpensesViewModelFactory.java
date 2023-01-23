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
    private String currentUserUid;

    TopExpensesViewModelFactory(String currentUserUid, ExpenseRepository expenseRepo) {
        this.currentUserUid = currentUserUid;
        this.expenseRepo = expenseRepo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(currentUserUid, expenseRepo);
    }

    public static Model getModel(String currentUserUid, FragmentActivity activity, ExpenseRepository expenseRepo) {
        return ViewModelProviders.of(activity, new TopExpensesViewModelFactory(currentUserUid, expenseRepo)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {

        public Model(String currentUserUid, ExpenseRepository expenseRepo) {
            super(currentUserUid, FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(currentUserUid).orderByChild("timestamp"), expenseRepo);
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(uid).orderByChild("timestamp")
                    .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
        }
    }
}