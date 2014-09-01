package cn.leslie.financemanager;

import java.util.Calendar;

/**
 * Helper class to handle some common functions about time.
 */
public class TimeUtility {
    public static final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private TimeUtility() {
        // make it as private.
    }

    public static int calculateDayOffset(long time1, long time2) {
        long offset = Calendar.getInstance().getTimeZone().getRawOffset();
        return (int) (((time1 + offset) / ONE_DAY_IN_MILLIS) - ((time2 + offset)/ ONE_DAY_IN_MILLIS));
    }

    private static long getBeginOfDay(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTimeInMillis();
    }

    /**
     * Get the time in milliseconds of the day specified.
     *
     * @param offset the offset of the week to current day.
     * @return the start time in milliseconds.
     */
    public static long getStartTimeOfDay(int offset) {
        Calendar now = Calendar.getInstance();
        if (offset != 0) {
            now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + offset);
        }
        return getBeginOfDay(now);
    }

    /**
     * Get the time in milliseconds of the week specified.
     *
     * @param offset the offset of the week to current week.
     * @return the start time in milliseconds.
     */
    public static long getStartTimeOfWeek(int offset) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // TODO: didn't figure out why this operation can not be done at first.
        if (offset != 0) {
            now.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR) + offset);
        }
        return getBeginOfDay(now);
    }

    /**
     * Get the time in milliseconds of the month specified.
     *
     * @param offset the offset of the week to current month.
     * @return the start time in milliseconds.
     */
    public static long getStartTimeOfMonth(int offset) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 1);
        if (offset != 0) {
            now.set(Calendar.MONTH, now.get(Calendar.MONTH) + offset);
        }
        return getBeginOfDay(now);
    }

    /**
     * Get the time in milliseconds of the year specified.
     *
     * @param offset the offset of the week to current year.
     * @return the start time in milliseconds.
     */
    public static long getStartTimeOfYear(int offset) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_YEAR, 1);
        if (offset != 0) {
            now.set(Calendar.YEAR, now.get(Calendar.YEAR) + offset);
        }
        return getBeginOfDay(now);
    }
}
