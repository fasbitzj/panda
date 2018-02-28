package com.jeecg.common;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by jiang.zheng on 2018/1/3.
 */
public class DateUtil {

    /**
     *
     * @param date format: yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date defaultDate(String date) {
        try {
            return CalendarUtil.defaultDateFormat().parse(date);
        } catch (ParseException e) {

        }
        return null;
    }
}
