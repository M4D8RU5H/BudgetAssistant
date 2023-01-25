package pl.project.budgetassistant.persistence.viewmodel_factories;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.persistence.viewmodels.UserProfileBaseViewModel;

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