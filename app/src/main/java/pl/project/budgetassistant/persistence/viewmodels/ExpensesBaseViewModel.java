package pl.project.budgetassistant.persistence.viewmodels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import java.util.Observable;

import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;

public class ExpensesBaseViewModel extends ViewModel implements java.util.Observer {
    protected ExpenseRepository expenseRepo;
    protected UpdateCommand updateCommand;

    public ExpensesBaseViewModel(LifecycleOwner lifecycleOwner, String currentUserUid) {
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
