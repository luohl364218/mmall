package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateStr, String format) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = formatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_FORMAT);
        DateTime dateTime = formatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String format) {
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static String dateToStr(Date date) {
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(DEFAULT_FORMAT);
    }

}
