package com.hairyworld.dms.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DmsUtils {
    public static final String LOCALDATE_PATTERN = "dd/MM/yyyy";
    public static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private DmsUtils() {
    }

    public static DateTime parseDate(final String dateTime) {
        try {
            return dateTime != null ? new DateTime(new SimpleDateFormat(DATETIME_PATTERN).parse(dateTime)) : null;
        } catch (final Exception e) {
            return null;
        }
    }

    public static String dateToString(final DateTime dateTime) {
        return dateTime != null ? new SimpleDateFormat(DATETIME_PATTERN).format(dateTime.toDate()) : null;
    }

    public static String dateToString(final LocalDate localDate) {
        return localDate != null ? new SimpleDateFormat(LOCALDATE_PATTERN).format(localDate.toDate()) : null;
    }

    public static String dateToString(final java.time.LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(LOCALDATE_PATTERN)) : null;
    }
}
