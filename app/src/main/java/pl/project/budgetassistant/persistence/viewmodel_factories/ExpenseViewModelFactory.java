package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import pl.project.budgetassistant.persistence.viewmodels.ExpenseBaseViewModel;

public class ExpenseViewModelFactory implements ViewModelProvider.Factory {
    private final String entryId;
    private final String uid;

    private ExpenseViewModelFactory(String uid, String entryId) {
        this.uid = uid;
        this.entryId = entryId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ExpenseBaseViewModel(uid, entryId);
    }

    public static ExpenseBaseViewModel getModel(String uid, String entryId, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new ExpenseViewModelFactory(uid, entryId)).get(ExpenseBaseViewModel.class);
    }
}