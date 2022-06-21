package com.telegram.reporting.utils;

import com.telegram.reporting.ExceptionHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DateTimeUtilsTest {
    private String expectedErrorMessage;
    private String validDate = "12.02.2012";
    private String validDateTime = "12.02.2012 04:35:15";


    @Test
    void parseDefaultDate_inputValid_success() {
        var expectedDate = LocalDate.of(2012, 2, 12);
        var parsedDate = DateTimeUtils.parseDefaultDate(validDate);
        assertEquals(expectedDate, parsedDate);
    }

    @Test
    void parseDefaultDate_inputNull_throwNullPointerException() {
        expectedErrorMessage = "Date string is required! Non null!";
        var exception = assertThrows(NullPointerException.class, () -> DateTimeUtils.parseDefaultDate(null));
        assertTrue(exception.getMessage().contains(expectedErrorMessage), "Can't find text in error message! Text: " + expectedErrorMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12/02/2012", "2-02-2012", "02-2012", "12-02-12", "2012-02-12"})
    void parseDefaultDate_badDateFormat_throwDateTimeException(String date) {
        var exception = assertThrows(DateTimeException.class, () -> DateTimeUtils.parseDefaultDate(date));
        var errorMessage = exception.getMessage();
        assertTrue(errorMessage.contains(date), "Can't find current input in error message. Input: " + date);
        assertTrue(errorMessage.contains(DateTimeUtils.DEFAULT_DATE_FORMAT), "Can't find format suggestion in error message. Format: " + DateTimeUtils.DEFAULT_DATE_FORMAT);
    }

    @Test
    void parseDefaultDateTime_inputValid_success() {
        var expectedDateTime = LocalDateTime.of(2012, 2, 12, 4, 35, 15);
        var parseDateTime = DateTimeUtils.parseDefaultDateTime(validDateTime);
        assertEquals(expectedDateTime, parseDateTime);
    }

    @Test
    void parseDefaultDateTime_inputNull_throwNullPointerException() {
        expectedErrorMessage = "DateTime string is required! Non null!";
        var exception = assertThrows(NullPointerException.class, () -> DateTimeUtils.parseDefaultDateTime(null));
        assertTrue(exception.getMessage().contains(expectedErrorMessage), "Can't find text in error message. Text: " + expectedErrorMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12-02-2012 4:35:15", "12-02-2012 04:35", "12/02/2012T04:35:15", "12/02/2012 04:35", "2-02-2012 04:35", "02-2012 04:35", "12-02-12 04:35", "2012-02-12 04:35"})
    void parseDefaultDateTime_badDateFormat_throwDateTimeException(String date) {
        var exception = assertThrows(DateTimeException.class, () -> DateTimeUtils.parseDefaultDateTime(date));
        var errorMessage = exception.getMessage();
        assertTrue(errorMessage.contains(date), "Can't find current input in error message. Input: " + date);
        assertTrue(errorMessage.contains(DateTimeUtils.DEFAULT_DATE_TIME_FORMAT), "Can't find format suggestion in error message. Format: " + DateTimeUtils.DEFAULT_DATE_TIME_FORMAT);
    }

    @Test
    void toDefaultFormat_inputDateValid_success() {
        var regexDatePattern = DateTimeUtils.DEFAULT_DATE_PATTERN;

        var date = DateTimeUtils.toDefaultFormat(LocalDate.now());

        var pattern = Pattern.compile(regexDatePattern);
        var matcher = pattern.matcher(date);

        assertTrue(matcher.matches(), "Wrong Date format! Expected: 'dd.MM.yyyy'. Current: " + date);
    }

    @Test
    void toDefaultFormat_inputDateNull_throwNullPointerException() {
        var exception = assertThrows(NullPointerException.class, () -> DateTimeUtils.toDefaultFormat((LocalDate) null));

        expectedErrorMessage = "Date is required! Non null!";
        assertTrue(exception.getMessage().contains(expectedErrorMessage), ExceptionHelper.cannotFindExpectedErrorMessage(expectedErrorMessage));
    }


    @Test
    void toDefaultFormat_inputDateTimeValid_success() {
        var regexDateTimePattern = DateTimeUtils.DEFAULT_DATE_TIME_PATTERN;

        var date = DateTimeUtils.toDefaultFormat(LocalDateTime.now());

        var pattern = Pattern.compile(regexDateTimePattern);
        var matcher = pattern.matcher(date);

        assertTrue(matcher.matches(), "Wrong DateTime format! Expected: 'dd.MM.yyyy HH:mm:ss'. Current: " + date);
    }

    @Test
    void toDefaultFormat_inputDateTimeNull_throwNullPointerException() {
        var exception = assertThrows(NullPointerException.class, () -> DateTimeUtils.toDefaultFormat((LocalDateTime) null));

        expectedErrorMessage = "DateTime is required! Non null!";
        assertTrue(exception.getMessage().contains(expectedErrorMessage), ExceptionHelper.cannotFindExpectedErrorMessage(expectedErrorMessage));
    }
}