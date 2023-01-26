package pl.project.budgetassistant.ui.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import pl.project.budgetassistant.ui.viewmodels.UserProfileBaseViewModel;

public class UserProfileViewModelFactory implements ViewModelProvider.Factory {
    private LifecycleOwner lifecycleOwner;
    private String currentUserUid;

    private UserProfileViewModelFactory(LifecycleOwner lifecycleOwner, String currentUserUid) {
        this.lifecycleOwner = lifecycleOwner;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserProfileBaseViewModel(lifecycleOwner, currentUserUid);
    }

    public static UserProfileBaseViewModel getModel(FragmentActivity activity, String currentUserUid) {
        return ViewModelProviders.of(activity, new UserProfileViewModelFactory(activity, currentUserUid)).get(UserProfileBaseViewModel.class);
    }
}