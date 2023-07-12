package com.hairyworld.dms.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DmsUtils {
    private static final DateTimeFormatter FORMATTER_LOCAL_DATE_JAVA_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATTER_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat FORMATTER_LOCAL_DATE = new SimpleDateFormat("dd/MM/yyyy");

    private DmsUtils() {
    }

    public static DateTime parseDate(final String dateTime) {
        try {
            return dateTime != null ? new DateTime(FORMATTER_DATE_TIME.parse(dateTime)) : null;
        } catch (final Exception e) {
            return null;
        }
    }

    public static String dateToString(final DateTime dateTime) {
        return dateTime != null ? FORMATTER_DATE_TIME.format(dateTime.toDate()) : null;
    }

    public static String dateToString(final LocalDate localDate) {
        return localDate != null ? FORMATTER_LOCAL_DATE.format(localDate.toDate()) : null;
    }

    public static String dateToString(final java.time.LocalDate localDate) {
        return localDate != null ? localDate.format(FORMATTER_LOCAL_DATE_JAVA_TIME) : null;
    }
}
