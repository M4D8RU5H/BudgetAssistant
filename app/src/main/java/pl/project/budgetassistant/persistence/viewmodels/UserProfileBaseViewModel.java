package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import java.util.Observable;

import pl.project.budgetassistant.persistence.repositories.UserRepository;

public class UserProfileBaseViewModel extends ViewModel implements java.util.Observer{
    protected UserRepository userRepo;
    protected UpdateCommand updateCommand;

    public UserProfileBaseViewModel(LifecycleOwner lifecycleOwner, String currentUserUid) {
        if (lifecycleOwner != null && currentUserUid != null) {
            userRepo = new UserRepository(lifecycleOwner, currentUserUid);
            userRepo.addObserver(this);
        }
    }

    public void setUpdateCommand(UpdateCommand updateCommand) {
        this.updateCommand = updateCommand;

        if (updateCommand != null) {
            updateCommand.execute();
        }
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        userRepo.setLifecycleOwner(lifecycleOwner);
    }

    public UserRepository getRepository() {
        return userRepo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (updateCommand != null) {
            updateCommand.execute();
        }
    }
}