package com.nobody.nobodyplace.utils;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    // 毫秒级别
    public static int SECOND = 1000;
    public static int MINUTE = SECOND * 60;
    public static int HOUR = MINUTE * 60;
    public static int DAY = HOUR * 24;

    private static final ZoneOffset CN_ZONE = ZoneOffset.of("+8");

    /**
     * 获取当天0点时间戳
     * @return 当天0点时间戳
     */
    public static long todayStartTimeMillis() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        return calendar.getTimeInMillis();
        LocalDate today = LocalDate.now();
        return today.atStartOfDay().toInstant(CN_ZONE).toEpochMilli();
    }

    /**
     * millisTime 是否在今天
     */
    public static boolean isToday(long millisTime) {
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        cal1.setTime(new Date(millisTime));
//        cal2.setTime(new Date(System.currentTimeMillis()));
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        LocalDateTime today = LocalDateTime.now(CN_ZONE);
        LocalDateTime target = LocalDateTime.ofEpochSecond(millisTime, 0, CN_ZONE);
        return today.getDayOfYear() == target.getDayOfYear() && today.getYear() == target.getYear();
    }

    /**
     * 两个时间戳是否为同一天
     * @param millisTime1 毫秒级别时间戳
     * @param millisTime2 毫秒级别时间戳
     * @return 毫秒级别时间戳
     */
    public static boolean isSameDay(long millisTime1, long millisTime2) {
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        cal1.setTime(new Date(millisTime1));
//        cal2.setTime(new Date(millisTime2));
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        LocalDateTime t1 = LocalDateTime.ofEpochSecond(millisTime1, 0, CN_ZONE);
        LocalDateTime t2 = LocalDateTime.ofEpochSecond(millisTime2, 0, CN_ZONE);
        return t1.getDayOfYear() == t2.getDayOfYear() && t1.getYear() == t2.getYear();
    }

    public static int toTimeStamp(String dateTime) {
//        long time = 0L;
//        try {
//            time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dateTime, new ParsePosition(0)).getTime() / 1000;
//        } catch (Exception e) {
//
//        }
//        // 秒级别
//        return (int) time;
        LocalDateTime t = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return (int) t.atZone(CN_ZONE).toEpochSecond();
    }

    public static void main(String[] args) {
        System.out.println(TimeUtil.todayStartTimeMillis());
        System.out.println(toTimeStamp("2012-10-22 23:02:10"));
    }
}
