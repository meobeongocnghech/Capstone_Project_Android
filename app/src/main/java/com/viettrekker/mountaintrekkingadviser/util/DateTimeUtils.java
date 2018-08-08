package com.viettrekker.mountaintrekkingadviser.util;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String formatISO(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = sdf.parse(sDate);
        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        return sdf.format(date);
    }

    public static String changeToLocale(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = sdf.parse(sDate);
        sdf.setTimeZone(TimeZone.getDefault());
        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        return sdf.format(date);
    }

    public static String changeToUTC(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = sdf.parse(sDate);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        return sdf.format(date);
    }

    public static int calculateAge(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = sdf.parse(sDate);
        Calendar age = Calendar.getInstance();
        age.setTimeInMillis(age.getTimeInMillis() - date.getTime());
        return age.get(Calendar.YEAR) - 1970;
    }

    public static String caculatorTime(long timeNow, long oldTime) throws ParseException {
        long diff = timeNow - oldTime;

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays > 0){
            return diffDays + " ngày";
        } else if (diffHours > 0){
            return diffHours + " giờ";
        } else if (diffMinutes > 0){
            return diffMinutes + " phút";
        } else {
            return "vài giây trước";
        }
    }
}
