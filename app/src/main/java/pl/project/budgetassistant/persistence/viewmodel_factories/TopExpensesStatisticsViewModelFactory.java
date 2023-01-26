package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import pl.project.budgetassistant.persistence.viewmodels.ExpensesBaseViewModel;

public class TopExpensesStatisticsViewModelFactory implements ViewModelProvider.Factory {
    private LifecycleOwner lifecycleOwner;
    private String currentUserUid;

    TopExpensesStatisticsViewModelFactory(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(lifecycleOwner, currentUserUid);
    }

    public static Model getModel(FragmentActivity activity, String currentUserUid) {
        return ViewModelProviders.of(activity, new TopExpensesStatisticsViewModelFactory(activity, currentUserUid)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {
        public Model(LifecycleOwner lifecycleOwner, String currentUserUid) {
            super(lifecycleOwner, currentUserUid);
        }
    }
}