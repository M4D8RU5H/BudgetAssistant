package pl.project.budgetassistant.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.project.budgetassistant.base.BaseFragment;
import pl.project.budgetassistant.persistence.repositories.ExpenseRepository;
import pl.project.budgetassistant.persistence.repositories.UserRepository;
import pl.project.budgetassistant.ui.viewmodels.UserProfileBaseViewModel;
import pl.project.budgetassistant.util.CalendarHelper;
import pl.project.budgetassistant.util.CategoriesHelper;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.ui.options.OptionsActivity;
import pl.project.budgetassistant.util.CurrencyHelper;
import pl.project.budgetassistant.R;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;
import pl.project.budgetassistant.ui.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.ui.viewmodel_factories.TopExpensesViewModelFactory;
import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.libraries.Gauge;
import pl.project.budgetassistant.models.Expense;

public class HomeFragment extends BaseFragment {
    private User user;
    private ListDataSet<Expense> expenses;
    private ExpenseRepository expenseRepo;
    private TopExpensesViewModelFactory.Model topExpensesViewModel;
    private UserProfileBaseViewModel userViewModel;
    private UserRepository userRepo;

    public static final CharSequence TITLE = "Podsumowanie";
    private Gauge gauge;
    private TopCategoriesAdapter adapter;
    private ArrayList<TopCategoryListViewModel> categoryModelsHome;
    private TextView totalBalanceTextView;
    private TextView gaugeLeftBalanceTextView;
    private TextView gaugeLeftLine1TextView;
    private TextView gaugeRightBalanceTextView;
    private TextView gaugeRightLine1TextView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        topExpensesViewModel = TopExpensesViewModelFactory.getModel(getActivity(), getCurrentUserUid());
        expenseRepo = topExpensesViewModel.getRepository();

        userViewModel = UserProfileViewModelFactory.getModel(getActivity(), getCurrentUserUid());
        userRepo = userViewModel.getRepository();

        categoryModelsHome = new ArrayList<>();

        gauge = view.findViewById(R.id.gauge);
        gauge.setValue(50);

        totalBalanceTextView = view.findViewById(R.id.total_balance_textview);
        gaugeLeftBalanceTextView = view.findViewById(R.id.gauge_left_balance_text_view);
        gaugeLeftLine1TextView = view.findViewById(R.id.gauge_left_line1_textview);
        gaugeRightBalanceTextView = view.findViewById(R.id.gauge_right_balance_text_view);
        gaugeRightLine1TextView = view.findViewById(R.id.gauge_right_line1_textview);

        ListView favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        userViewModel.setUpdateCommand(() -> {
            user = userRepo.getCurrentUser();
            if (user == null) return;

            Calendar startDate = CalendarHelper.getUserPeriodStartDate(user);
            Calendar endDate = CalendarHelper.getUserPeriodEndDate(user);

            dataUpdated();

            topExpensesViewModel.setUpdateCommand(() -> {
                expenses = expenseRepo.getFromDateRange(startDate, endDate);
                dataUpdated();
            });
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options:
                startActivity(new Intent(getActivity(), OptionsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dataUpdated() {
        user = userRepo.getCurrentUser();

        if (user == null || expenses == null || getContext() == null) return;

        List<Expense> expenseList = new ArrayList<>(expenses.getList());

        Calendar startDate = CalendarHelper.getUserPeriodStartDate(user);
        Calendar endDate = CalendarHelper.getUserPeriodEndDate(user);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM");

        long expensesSumInDateRange = 0;
        long incomesSumInDateRange = 0;

        HashMap<Category, Long> categoryModels = new HashMap<>();
        for (Expense expense : expenseList) {
            if (expense.amount > 0) {
                incomesSumInDateRange += expense.amount;
                continue;
            }
            expensesSumInDateRange += expense.amount;
            Category category = CategoriesHelper.searchCategory(expense.categoryId);
            if (categoryModels.get(category) != null)
                categoryModels.put(category, categoryModels.get(category) + expense.amount);
            else
                categoryModels.put(category, expense.amount);
        }

        categoryModelsHome.clear();
        for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
            categoryModelsHome.add(new TopCategoryListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(getContext()),
                    user.currency, categoryModel.getValue()));
        }

        Collections.sort(categoryModelsHome, new Comparator<TopCategoryListViewModel>() {
            @Override
            public int compare(TopCategoryListViewModel o1, TopCategoryListViewModel o2) {
                return Long.compare(o1.getMoney(), o2.getMoney());
            }
        });

        adapter.notifyDataSetChanged();
        totalBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, user.budget.amountToSpend + user.budget.spentAmount));

        gaugeLeftBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, user.budget.spentAmount));
        gaugeLeftLine1TextView.setText(dateFormat.format(startDate.getTime()));
        gaugeRightBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, user.budget.amountToSpend));
        gaugeRightLine1TextView.setText(dateFormat.format(endDate.getTime()));

        gauge.setPointStartColor(ContextCompat.getColor(getContext(), R.color.gauge_white));
        gauge.setPointEndColor(ContextCompat.getColor(getContext(), R.color.gauge_white));
        gauge.setStrokeColor(ContextCompat.getColor(getContext(), R.color.gauge_gray));

        long limit = user.budget.amountToSpend;
        long expenses = -expensesSumInDateRange;
        int percentage = (int) (expenses * 100 / (double) limit);
        if (percentage > 100) percentage = 100;
        gauge.setValue(percentage);
    }
}
