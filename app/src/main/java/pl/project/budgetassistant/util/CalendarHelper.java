package pl.project.budgetassistant.util;

import java.util.Calendar;
import java.util.Date;

import pl.project.budgetassistant.firebase.models.User;
import pl.project.budgetassistant.firebase.models.UserSettings;

public class CalendarHelper {
    public static Calendar getUserPeriodStartDate(User user) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(getUserFirstDayOfWeek(user));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, user.userSettings.dayOfMonthStart + 1);

        if (new Date().getTime() < cal.getTime().getTime())
            cal.add(Calendar.MONTH, -1);

        return cal;
    }

    public static Calendar getUserPeriodEndDate(User user) {
        Calendar cal = getUserPeriodStartDate(user);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);

        return cal;
    }

    private static int getUserFirstDayOfWeek(User user) {
        switch (user.userSettings.dayOfWeekStart) {
            case 0:
                return Calendar.MONDAY;
            case 1:
                return Calendar.TUESDAY;
            case 2:
                return Calendar.WEDNESDAY;
            case 3:
                return Calendar.THURSDAY;
            case 4:
                return Calendar.FRIDAY;
            case 5:
                return Calendar.SATURDAY;
            case 6:
                return Calendar.SUNDAY;
        }
        return 0;
    }
}
