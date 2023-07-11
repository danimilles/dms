package com.hairyworld.dms.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DMSUtils {
    private static final DateTimeFormatter FORMATTER_LOCAL_DATE_JAVA_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATTER_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat FORMATTER_LOCAL_DATE = new SimpleDateFormat("dd/MM/yyyy");

    private DMSUtils() {
        }

       public static DateTime parseDate(final String dateTime) {
           try {
               return new DateTime(FORMATTER_DATE_TIME.parse(dateTime));
           } catch (final Exception e) {
               return null;
           }
       }

       public static String dateToString(final DateTime dateTime) {
            return FORMATTER_DATE_TIME.format(dateTime.toDate());
       }

       public static String dateToString(final LocalDate localDate) {
            return FORMATTER_LOCAL_DATE.format(localDate.toDate());
       }

       public static String dateToString(final java.time.LocalDate localDate) {
            return localDate.format(FORMATTER_LOCAL_DATE_JAVA_TIME);
       }
}
