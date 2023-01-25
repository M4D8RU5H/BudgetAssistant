package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Observable;

import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.FirebaseQueryLiveDataElement;
import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.repositories.UpdateCommand;

public class ExpenseBaseViewModel extends ViewModel implements java.util.Observer {
    protected ExpenseRepository expenseRepo;
    protected UpdateCommand updateCommand;

    public ExpenseBaseViewModel(LifecycleOwner lifecycleOwner, String currentUserUid) {
        if (lifecycleOwner != null && currentUserUid != null) {
            expenseRepo = new ExpenseRepository(lifecycleOwner, currentUserUid);
            expenseRepo.addObserver(this);
        }
    }

    public void setUpdateCommand(UpdateCommand updateCommand) {
        this.updateCommand = updateCommand;
        updateCommand.execute();
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        expenseRepo.setLifecycleOwner(lifecycleOwner);
    }

    public ExpenseRepository getRepository() {
        return expenseRepo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (updateCommand != null) {
            updateCommand.execute();
        }
    }
}
