package com.telegram.reporting.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

public class DateTimeUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DEFAULT_DATE_PATTERN = "((\\d{2}).(\\d{2}).(\\d{4}))";
    private static final String DEFAULT_DATE_TIME_PATTERN = "((\\d{2}).(\\d{2}).(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2}))";
    private static final String DATE_TYPE = "Date";
    private static final String DATE_TIME_TYPE = "DateTime";

    public static LocalDate parseDefaultDate(String date) {
        checkStringInputFormat(date, DEFAULT_DATE_PATTERN, DATE_TYPE);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static LocalDateTime parseDefaultDateTime(String dateTime) {
        checkStringInputFormat(dateTime, DEFAULT_DATE_TIME_PATTERN, DATE_TIME_TYPE);
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    public static String toDefaultFormat(LocalDate date) {
        Objects.requireNonNull(date, "Date is required! Non null!");
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static String toDefaultFormat(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "DateTime is required! Non null!");
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    private static void checkStringInputFormat(String input, String regexPattern, String handleType) {
        Objects.requireNonNull(input, handleType + " string is required! Non null!");

        var pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            String format = getFormat(handleType);
            throw new DateTimeException(String.format("Incorrect %s format. Use default format: %s. Current value = %s", handleType, format, input));
        }
    }

    private static String getFormat(String type) {
        return switch (type) {
            case "Date" -> DEFAULT_DATE_FORMAT;
            case "DateTime" -> DEFAULT_DATE_TIME_FORMAT;
            default -> "Unrecognized type! Input: " + type;
        };
    }
}
