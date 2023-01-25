package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import pl.project.budgetassistant.persistence.viewmodels.ExpenseBaseViewModel;

public class ExpenseViewModelFactory implements ViewModelProvider.Factory {
    private LifecycleOwner lifecycleOwner;
    private String currentUserUid;

    private ExpenseViewModelFactory(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ExpenseBaseViewModel(lifecycleOwner, currentUserUid);
    }

    public static ExpenseBaseViewModel getModel(FragmentActivity activity, String currentUserUid) {
        return ViewModelProviders.of(activity, new ExpenseViewModelFactory(activity, currentUserUid)).get(ExpenseBaseViewModel.class);
    }
}