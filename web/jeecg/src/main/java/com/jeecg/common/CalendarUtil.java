package com.jeecg.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jiang.zheng on 2018/1/2.
 */
public class CalendarUtil {
    private static final String default_date_time_format = "yyyy-MM-dd HH:mm:ss";

    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat(default_date_time_format);
        return sdf.format(new Date());
    }

    /**
     * 格式化到小时
     * @param date
     * @return
     */
    public static String hoursStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return defaultDateFormat().format(calendar.getTime());
    }

    /**
     * 格式化到小时
     * @param date
     * @return
     */
    public static String hoursEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return defaultDateFormat().format(calendar.getTime());
    }

    /**
     * 几个小时前格式化到小时的时间
     * @param beforeHours
     * @return
     */
    public static String beforeHoursEndTime(int beforeHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -1 * beforeHours);
        return hoursEndTime(calendar.getTime());
    }

    /**
     * 获取默认format
     * @return
     */
    public static SimpleDateFormat defaultDateFormat() {
        return new SimpleDateFormat(default_date_time_format);
    }

    /**
     * 格式化到天的开始时间  时分秒为00:00:00
     * @param date
     * @return
     */
    public static String daysStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return defaultDateFormat().format(calendar.getTime());
    }

    /**
     * 格式化到天的结束时间  时分秒为00:00:00
     * @param date
     * @return
     */
    public static String daysEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return defaultDateFormat().format(calendar.getTime());
    }

    /**
     * 格式化到天的开始时间  时分秒为00:00:00
     * @param date
     * @return
     */
    public static Date daysStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static void main(String[] args) {
//        System.out.println(now());
        System.out.println(hoursStartTime(new Date()));
        System.out.println(hoursEndTime(new Date()));
        System.out.println(daysStartTime(new Date()));
        System.out.println(daysEndTime(new Date()));
    }
}
