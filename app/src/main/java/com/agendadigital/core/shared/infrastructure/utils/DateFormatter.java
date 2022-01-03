package com.agendadigital.core.shared.infrastructure.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static Date parse(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static String formatToDate(Date date) {
        return dateFormat.format(date);
    }

    public static String formatToTime(Date date) {
        return  timeFormat.format(date);
    }
}
