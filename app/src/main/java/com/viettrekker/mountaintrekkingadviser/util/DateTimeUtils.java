package com.viettrekker.mountaintrekkingadviser.util;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        if (diffDays > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (diffDays > 2) {
                Date date = new Date(oldTime);
                return sdf.format(date);
            }
            return diffDays + " ngày";
        } else if (diffHours > 0) {
            return diffHours + " giờ";
        } else if (diffMinutes > 0) {
            return diffMinutes + " phút";
        } else {
            return "vài giây trước";
        }
    }

    public static String parseStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String parseStringTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("hh:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static Date parseDateTimeToString(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy hh:mm a");
        return sdf.parse(sDate);
    }

    public static String parseStringDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd");
        return sdf.format(date);
    }

    public static String parseStringDayinWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("EEE");
        return sdf.format(date);
    }

    public static Date parseStringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("EEE, MMM dd yyyy HH:mm:ss");
        Date newDate = null;
        try {
            newDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static Date changeTimeToLocale(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (sDate.contains("Z")) {
            sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            Date date = sdf.parse(sDate);
            return date;
        } else {
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(sDate);
            return date;
        }
    }

    public static String caculatorStringTime(Date oldTime, Date timeNow) throws ParseException {
        long diff = timeNow.getTime() - oldTime.getTime();

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays > 0) {
            return diffDays + " ngày";
        } else if (diffHours > 0) {
            return diffHours + " giờ";
        } else if (diffMinutes > 0) {
            return diffMinutes + " phút";
        } else {
            return "vài giây";
        }
    }

    public static String change24to12Format(String sTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("HH:mm");
        Date date = sdf.parse(sTime);
        sdf.applyPattern("hh:mm a");
        return sdf.format(date);
    }

//    public static void main(String[] args) throws Exception{
////        Date d = changeTimeToLocale("2018-08-19 14:02");
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
////        System.out.println(parseStringDate(d) + " " + parseStringTime(d));
//        System.out.println(sdf.parse("19-08-2018 14:02:00"));
//    }

}
