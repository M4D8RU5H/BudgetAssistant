package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import pl.project.budgetassistant.persistence.viewmodels.ExpensesBaseViewModel;

public class TopExpensesViewModelFactory implements ViewModelProvider.Factory {
    private LifecycleOwner lifecycleOwner;
    private String currentUserUid;

    TopExpensesViewModelFactory(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(lifecycleOwner, currentUserUid);
    }

    public static Model getModel(FragmentActivity activity, String currentUserUid) {
        Model model = ViewModelProviders.of(activity, new TopExpensesViewModelFactory(activity, currentUserUid)).get(Model.class);
        model.setLifecycleOwner(activity);
        return model;
    }

    public static class Model extends ExpensesBaseViewModel {
        public Model(LifecycleOwner lifecycleOwner, String currentUserUid) {
            super(lifecycleOwner, currentUserUid);
        }
    }
}