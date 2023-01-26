package pl.project.budgetassistant.ui.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;

import pl.project.budgetassistant.ui.viewmodels.ExpensesBaseViewModel;

public class ExpensesHistoryViewModelFactory implements ViewModelProvider.Factory {
    private LifecycleOwner lifecycleOwner;
    private String currentUserUid;

    ExpensesHistoryViewModelFactory(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(lifecycleOwner, currentUserUid);
    }

    public static Model getModel(FragmentActivity activity, String currentUserUid) {
        return ViewModelProviders.of(activity, new ExpensesHistoryViewModelFactory(activity, currentUserUid)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {
        private Calendar endDate;
        private Calendar startDate;

        public Model(LifecycleOwner lifecycleOwner, String currentUserUid) {
            super(lifecycleOwner, currentUserUid);
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