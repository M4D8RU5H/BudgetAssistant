package pl.project.budgetassistant.ui.main.statistics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.base.BaseFragment;
import pl.project.budgetassistant.persistence.firebase.QueryResult;
import pl.project.budgetassistant.persistence.firebase.FirebaseObserver;
import pl.project.budgetassistant.persistence.firebase.ListDataSet;
import pl.project.budgetassistant.models.User;
import pl.project.budgetassistant.models.Expense;
import pl.project.budgetassistant.persistence.viewmodel_factories.TopExpensesStatisticsViewModelFactory;
import pl.project.budgetassistant.persistence.viewmodel_factories.UserProfileViewModelFactory;
import pl.project.budgetassistant.util.CalendarHelper;
import pl.project.budgetassistant.util.CategoriesHelper;
import pl.project.budgetassistant.models.Category;
import pl.project.budgetassistant.ui.options.OptionsActivity;
import pl.project.budgetassistant.util.CurrencyHelper;

public class StatisticsFragment extends BaseFragment {
    public static final CharSequence TITLE = "Statystyki";

    private Menu menu;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private User user;
    private ListDataSet<Expense> expenseListDataSet;
    private PieChart pieChart;
    private ArrayList<TopCategoryStatisticsListViewModel> categoryModelsHome;
    private TopCategoriesStatisticsAdapter adapter;
    private TextView dividerTextView;
    private TextView expensesTextView;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pieChart = view.findViewById(R.id.pie_chart);
        dividerTextView = view.findViewById(R.id.divider_textview);
        View incomesExpensesView = view.findViewById(R.id.incomes_expenses_view);
        expensesTextView = incomesExpensesView.findViewById(R.id.expenses_textview);

        categoryModelsHome = new ArrayList<>();
        ListView favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesStatisticsAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        TopExpensesStatisticsViewModelFactory.getModel(getCurrentUserUid(), getActivity()).observe(this, new FirebaseObserver<QueryResult<ListDataSet<Expense>>>() {
            @Override
            public void onChanged(QueryResult<ListDataSet<Expense>> queryResult) {
                if (queryResult.hasNoError()) {
                    StatisticsFragment.this.expenseListDataSet = queryResult.getResult();
                    dataUpdated();
                }
            }
        });


        UserProfileViewModelFactory.getModel(getCurrentUserUid(), getActivity()).observe(this, new FirebaseObserver<QueryResult<User>>() {
            @Override
            public void onChanged(QueryResult<User> queryResult) {
                if (queryResult.hasNoError()) {
                    StatisticsFragment.this.user = queryResult.getResult();

                    calendarStart = CalendarHelper.getUserPeriodStartDate(user);
                    calendarEnd = CalendarHelper.getUserPeriodEndDate(user);

                    updateCalendarIcon(false);
                    calendarUpdated();
                    dataUpdated();
                }
            }
        });
    }


    private void dataUpdated() {
        if (calendarStart != null && calendarEnd != null && expenseListDataSet != null) {
            List<Expense> expenses = new ArrayList<>(expenseListDataSet.getList());

            long expensesSumInDateRange = 0;

            HashMap<Category, Long> categoryModels = new HashMap<>();
            for (Expense expense : expenses) {
                expensesSumInDateRange += expense.amount;
                Category category = CategoriesHelper.searchCategory(expense.categoryId);
                if (categoryModels.get(category) != null)
                    categoryModels.put(category, categoryModels.get(category) + expense.amount);
                else
                    categoryModels.put(category, expense.amount);

            }

            categoryModelsHome.clear();

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<Integer> pieColors = new ArrayList<>();

            for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
                float percentage = categoryModel.getValue() / (float) expensesSumInDateRange;
                final float minPercentageToShowLabelOnChart = 0.1f;
                categoryModelsHome.add(new TopCategoryStatisticsListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(getContext()),
                        user.currency, categoryModel.getValue(), percentage));
                if (percentage > minPercentageToShowLabelOnChart) {
                    Drawable drawable = getContext().getDrawable(categoryModel.getKey().getIconResourceID());
                    drawable.setTint(Color.parseColor("#FFFFFF"));
                    pieEntries.add(new PieEntry(-categoryModel.getValue(), drawable));

                } else {
                    pieEntries.add(new PieEntry(-categoryModel.getValue()));
                }
                pieColors.add(categoryModel.getKey().getIconColor());
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setDrawValues(false);
            pieDataSet.setColors(pieColors);
            pieDataSet.setSliceSpace(2f);

            PieData data = new PieData(pieDataSet);
            pieChart.setData(data);
            pieChart.setTouchEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.getDescription().setEnabled(false);

            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.backgroundPrimary));
            pieChart.setHoleRadius(55f);
            pieChart.setTransparentCircleRadius(55f);
            pieChart.setDrawCenterText(true);
            pieChart.setRotationAngle(270);
            pieChart.setRotationEnabled(false);
            pieChart.setHighlightPerTapEnabled(true);

            pieChart.invalidate();

            Collections.sort(categoryModelsHome, new Comparator<TopCategoryStatisticsListViewModel>() {
                @Override
                public int compare(TopCategoryStatisticsListViewModel o1, TopCategoryStatisticsListViewModel o2) {
                    return Long.compare(o1.getMoney(), o2.getMoney());
                }
            });


            adapter.notifyDataSetChanged();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            dividerTextView.setText("Przedział czasowy: " + dateFormat.format(calendarStart.getTime())
                    + "  -  " + dateFormat.format(calendarEnd.getTime()));

            expensesTextView.setText(CurrencyHelper.formatCurrency(user.currency, expensesSumInDateRange));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_fragment_menu, menu);
        this.menu = menu;
        updateCalendarIcon(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateCalendarIcon(boolean updatedFromUI) {
        if (menu == null) return;
        MenuItem calendarIcon = menu.findItem(R.id.action_date_range);
        if (calendarIcon == null) return;
        if (updatedFromUI) {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar_active));
        } else {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_range:
                showSelectDateRangeDialog();
                return true;
            case R.id.action_options:
                startActivity(new Intent(getActivity(), OptionsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSelectDateRangeDialog() {
        SmoothDateRangePickerFragment datePicker = SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
            @Override
            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart, int yearEnd, int monthEnd, int dayEnd) {
                calendarStart = Calendar.getInstance();
                calendarStart.set(yearStart, monthStart, dayStart);
                calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                calendarStart.set(Calendar.MINUTE, 0);
                calendarStart.set(Calendar.SECOND, 0);

                calendarEnd = Calendar.getInstance();
                calendarEnd.set(yearEnd, monthEnd, dayEnd);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
                calendarEnd.set(Calendar.MINUTE, 59);
                calendarEnd.set(Calendar.SECOND, 59);
                calendarUpdated();
                updateCalendarIcon(true);
            }
        });
        datePicker.show(getActivity().getFragmentManager(), "TAG");
    }

    private void calendarUpdated() {
        TopExpensesStatisticsViewModelFactory.getModel(getCurrentUserUid(), getActivity()).setDateFilter(calendarStart, calendarEnd);
    }
}
