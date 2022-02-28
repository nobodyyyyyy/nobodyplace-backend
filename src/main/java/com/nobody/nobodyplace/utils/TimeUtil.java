package com.nobody.nobodyplace.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    // 毫秒级别
    public static int MILLI_SECOND = 1000;
    public static int MILLI_MINUTE = MILLI_SECOND * 60;
    public static int MILLI_HOUR = MILLI_MINUTE * 60;
    public static int MILLI_DAY = MILLI_HOUR * 24;

    public static int SEC_MINUTE = 60;
    public static int SEC_HOUR = SEC_MINUTE * 60;
    public static int SEC_DAY = SEC_HOUR * 24;

    /**
     * 获取当天0点时间戳
     * @return 当天0点时间戳
     */
    public static long todayStartTimeMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * millisTime 是否在今天
     */
    public static boolean isToday(long millisTime) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(millisTime));
        cal2.setTime(new Date(System.currentTimeMillis()));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayStartTimeSeconds(int time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date((long)time * 1000));
        cal.set(Calendar.HOUR_OF_DAY, 0); // do not use HOUR
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return (int)(cal.getTimeInMillis() / 1000);
    }

    public static long getDayStartTimeMillis(int time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date((long)time * 1000));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 两个时间戳是否为同一天
     * @param time1 秒级别时间戳
     * @param time2 秒级别时间戳
     */
    public static boolean isSameDay(int time1, int time2) {
        return isSameDay((long)time1 * 1000, (long)time2 * 1000);
    }

    /**
     * 两个时间戳是否为同一天
     * @param millisTime1 毫秒级别时间戳
     * @param millisTime2 毫秒级别时间戳
     */
    public static boolean isSameDay(long millisTime1, long millisTime2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(millisTime1));
        cal2.setTime(new Date(millisTime2));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int toTimeStampSeconds(String dateTime) {
        long time = 0L;
        try {
            time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dateTime, new ParsePosition(0)).getTime() / 1000;
        } catch (Exception e) {

        }
        // 秒级别
        return (int) time;
    }

    public static void main(String[] args) {
        System.out.println(getDayStartTimeSeconds(1642017617));
    }
}
