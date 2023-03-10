package pl.project.budgetassistant.ui.main.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.project.budgetassistant.R;
import pl.project.budgetassistant.ui.viewmodel_factories.ExpensesHistoryViewModelFactory;
import pl.project.budgetassistant.base.BaseFragment;
import pl.project.budgetassistant.ui.options.OptionsActivity;

public class HistoryFragment extends BaseFragment {
    public static final CharSequence TITLE = "Historia";
    Calendar calendarStart;
    Calendar calendarEnd;
    private RecyclerView historyRecyclerView;
    private ExpensesRecyclerViewAdapter historyRecyclerViewAdapter;
    private Menu menu;
    private TextView dividerTextView;
    private ExpensesHistoryViewModelFactory.Model expenseHistoryViewModel;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expenseHistoryViewModel = ExpensesHistoryViewModelFactory.getModel(getActivity(), getCurrentUserUid());

        dividerTextView = view.findViewById(R.id.divider_textview);
        dividerTextView.setText("OStatnie 100 element??w:");
        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        historyRecyclerViewAdapter = new ExpensesRecyclerViewAdapter(getActivity(), getCurrentUserUid());
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);

        historyRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                historyRecyclerView.smoothScrollToPosition(0);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_fragment_menu, menu);
        this.menu = menu;
        updateCalendarIcon();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_range:
                showSelectDateRangeDialog();
                return true;
            case R.id.action_options:
                startActivity(new Intent(getActivity(), OptionsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCalendarIcon() {
        MenuItem calendarIcon = menu.findItem(R.id.action_date_range);
        if (calendarIcon == null) return;

        if (expenseHistoryViewModel.hasDateSet()) {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar_active));

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            dividerTextView.setText("Przedzia?? czasowy: " + dateFormat.format(expenseHistoryViewModel.getStartDate().getTime())
                    + "  -  " + dateFormat.format(expenseHistoryViewModel.getEndDate().getTime()));
        } else {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar));

            dividerTextView.setText("Ostatnie 100 element??w:");
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
                updateCalendarIcon();
            }
        });
        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                calendarStart = null;
                calendarEnd = null;
                calendarUpdated();
                updateCalendarIcon();
            }
        });
        datePicker.show(getActivity().getFragmentManager(), "TAG");
    }

    private void calendarUpdated() {
        historyRecyclerViewAdapter.setDateRange(calendarStart, calendarEnd);
    }

}
