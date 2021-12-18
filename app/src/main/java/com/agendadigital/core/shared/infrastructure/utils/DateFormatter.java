package com.agendadigital.core.shared.infrastructure.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parse(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static String format(Date date) {
        return dateFormat.format(date);
    }
}
