package com.telegram.reporting.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateTimeUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static LocalDate parseLocalDate(String date) {
        Objects.requireNonNull(date, "Date string is required! Non null!");
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static LocalDate parseLocalDateTime(String dateTime) {
        Objects.requireNonNull(dateTime, "Date string is required! Non null!");
        return LocalDate.parse(dateTime, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    public static String toDefaultFormat(LocalDate date) {
        Objects.requireNonNull(date, "Date is required! Non null!");
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static String toDefaultFormat(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "Date is required! Non null!");
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

}
