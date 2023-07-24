package com.hairyworld.dms.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DmsUtils {
    public static final String LOCALDATE_PATTERN = "dd/MM/yyyy";
    public static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    public static final String DATETIME_WO_SECONDS_PATTERN = "dd/MM/yyyy HH:mm";

    private DmsUtils() {
    }

    public static DateTime parseDate(final String dateTime) {
        try {
            return dateTime != null ? new DateTime(new SimpleDateFormat(DATETIME_PATTERN).parse(dateTime))
                    .withZoneRetainFields(DateTimeZone.forID("Europe/Madrid")) : null;
        } catch (final Exception e) {
            try {
                return new DateTime(new SimpleDateFormat(DATETIME_WO_SECONDS_PATTERN).parse(dateTime));
            } catch (final Exception ex) {
                return null;
            }
        }
    }

    public static String dateToString(final DateTime dateTime) {
        return dateTime != null ? new SimpleDateFormat(DATETIME_PATTERN).format(dateTime
                .withZoneRetainFields(DateTimeZone.forID("Europe/Madrid")).toDate()) : null;
    }

    public static String dateToString(final LocalDate localDate) {
        return localDate != null ? new SimpleDateFormat(LOCALDATE_PATTERN).format(localDate.toDate()) : null;
    }

    public static String dateToString(final java.time.LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay().format(DateTimeFormatter.ofPattern(LOCALDATE_PATTERN)) : null;
    }
}
