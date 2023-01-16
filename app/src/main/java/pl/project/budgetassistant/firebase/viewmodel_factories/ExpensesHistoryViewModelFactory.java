package pl.project.budgetassistant.firebase.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.project.budgetassistant.firebase.viewmodels.ExpensesBaseViewModel;

public class ExpensesHistoryViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    ExpensesHistoryViewModelFactory(String uid) {
        this.uid = uid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);
    }

    public static Model getModel(String uid, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new ExpensesHistoryViewModelFactory(uid)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {

        private Calendar endDate;
        private Calendar startDate;

        public Model(String uid) {
            super(uid, getDefaultQuery(uid));
        }

        private static Query getDefaultQuery(String uid) {
            return FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(uid).orderByChild("timestamp").limitToFirst(500);
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            if (startDate != null && endDate != null) {
                liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                        .child("expenses").child(uid).orderByChild("timestamp")
                        .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
            } else {
                liveData.setQuery(getDefaultQuery(uid));
            }
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