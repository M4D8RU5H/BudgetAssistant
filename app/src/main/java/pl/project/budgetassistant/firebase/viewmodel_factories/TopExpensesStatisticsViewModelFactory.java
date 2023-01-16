package pl.project.budgetassistant.firebase.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import pl.project.budgetassistant.firebase.viewmodels.ExpensesBaseViewModel;

public class TopExpensesStatisticsViewModelFactory implements ViewModelProvider.Factory {
    private Calendar endDate;
    private Calendar startDate;
    private String uid;

    TopExpensesStatisticsViewModelFactory(String uid) {
        this.uid = uid;


    }
    public void setDate(Calendar startDate, Calendar endDate){
        this.startDate=startDate;
        this.endDate=endDate;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);
    }

    public static Model getModel(String uid, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new TopExpensesStatisticsViewModelFactory(uid)).get(Model.class);
    }

    public static class Model extends ExpensesBaseViewModel {

        public Model(String uid) {
            super(uid, FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(uid).orderByChild("timestamp"));
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                    .child("expenses").child(uid).orderByChild("timestamp")
                    .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
        }
    }
}