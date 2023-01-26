package pl.project.budgetassistant.ui.main;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import pl.project.budgetassistant.ui.main.statistics.StatisticsFragment;
import pl.project.budgetassistant.ui.main.history.HistoryFragment;
import pl.project.budgetassistant.ui.main.home.HomeFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static int TAB_COUNT = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return StatisticsFragment.newInstance();
            case 1:
                return HistoryFragment.newInstance();
            case 2:
                return HomeFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return StatisticsFragment.TITLE;

            case 1:
                return HistoryFragment.TITLE;

            case 2:
                return HomeFragment.TITLE;
        }
        return super.getPageTitle(position);
    }
}