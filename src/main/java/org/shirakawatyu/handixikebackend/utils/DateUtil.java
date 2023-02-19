package org.shirakawatyu.handixikebackend.utils;

import org.shirakawatyu.handixikebackend.common.Const;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    public static long getDate(String source) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(source).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String totalWeek() {
        long week = (Const.END_DATE - Const.START_DATE) / (1000 * 60 * 60 * 24 * 7);
        return Long.toString(week);
    }

    public static String curWeek() {
        long cur = (System.currentTimeMillis() - Const.START_DATE) / (1000 * 60 * 60 * 24 * 7) + 1;
//        if (cur > Long.parseLong(totalWeek())) return totalWeek();
        return Long.toString(cur);
    }
    public static String getCurFormatDate() {
        DateFormat gmtDateFormat = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss z ", Locale.ENGLISH);
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String format = gmtDateFormat.format(new Date());
        return format.replace("GMT+08:00", "GMT+0800").trim();
    }
}
