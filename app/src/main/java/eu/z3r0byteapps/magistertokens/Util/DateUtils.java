package eu.z3r0byteapps.magistertokens.Util;

/**
 * Created by z3r0byte on 4-3-17.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String formatDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date parseDate(String date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        Date error = new Date(2000, 12, 31);
        return error;
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.getTimeInMillis());
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return new Date(calendar.getTimeInMillis());
    }

    public static Integer diffDays(Date date1, Date date2) {
        return (int) Math.ceil((double) (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Boolean isAfter(Date date1, Date date2) {
        if (date2.getTime() - date1.getTime() > 0) {
            return true;
        } else {
            return false;
        }
    }
}