package com.nobody.nobodyplace.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    // 毫秒级别
    public static int SECOND = 1000;
    public static int MINUTE = SECOND * 60;
    public static int HOUR = MINUTE * 60;
    public static int DAY = HOUR * 24;

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

    /**
     * 两个时间戳是否为同一天
     * @param millisTime1 毫秒级别时间戳
     * @param millisTime2 毫秒级别时间戳
     * @return 毫秒级别时间戳
     */
    public static boolean isSameDay(long millisTime1, long millisTime2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(millisTime1));
        cal2.setTime(new Date(millisTime2));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int toTimeStamp(String dateTime) {
        long time = 0L;
        try {
            time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dateTime, new ParsePosition(0)).getTime() / 1000;
        } catch (Exception e) {

        }
        // 秒级别
        return (int) time;
    }
}
