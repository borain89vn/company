package jp.co.zensho.android.sukiya.common;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    public static Calendar today() {
        Calendar cal = Calendar.getInstance();
        return cal;
    }
    
    public static Calendar todayOnlyDate() {
        Calendar cal = DateUtils.today();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
    
    public static Calendar dateOnly(Date d) {
        Calendar cal = DateUtils.today();
        cal.setTime(d);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
    
    public static Date date(int y, int m, int d, int h, int mi, int s) {
        Calendar cal = DateUtils.today();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, mi);
        cal.set(Calendar.SECOND, s);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static String getYYYYMMDD(Date date) {
        Calendar cal = DateUtils.today();
        cal.setTime(date);
        
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d%02d%02d", y, m, d);
    }
}
