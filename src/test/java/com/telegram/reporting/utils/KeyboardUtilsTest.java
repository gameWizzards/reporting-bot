package com.telegram.reporting.utils;

import lombok.val;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
class KeyboardUtilsTest {
    private final String buttonName1 = "Some button1";
    private final String buttonName2 = "Some button2";

    @Test
    public void createButton_validButtonName_success() {
        var button = KeyboardUtils.createSimpleButton(buttonName1);
        assertTrue(button.contains(buttonName1), "Can't find button with assigning name: %s".formatted(buttonName1));
    }

    @Test
    public void createButton_inputEmpty_provideCustomErrorMessage() {
        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createSimpleButton(""));
        assertEquals(exception.getMessage(), "Can't create button without name");
    }

    @Test
    public void createButton_inputNull_provideCustomErrorMessage() {
        var exception = assertThrows(NullPointerException.class, () -> KeyboardUtils.createSimpleButton(null));
        assertEquals(exception.getMessage(), "Can't create button without name");
    }

    @Test
    public void createRowButtons_validButtonNames_success() {
//        String[] buttonNames = {buttonName1, buttonName2};
//        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
//        assertEquals(buttonNames.length, rowButtons.size(), "Expected quantity of buttons don't create.");
//        assertTrue(rowButtons.contains(buttonName1), "Can't find button with assigning name: %s".formatted(buttonName1));
//        assertTrue(rowButtons.contains(buttonName2), "Can't find button with assigning name: %s".formatted(buttonName2));
    }

    @Test
    public void createRowButtons_arrayNamesContainNull_provideCustomErrorMessage() {
//        String[] buttonsNames = {buttonName1, buttonName2, null};
//        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createRowButtons(buttonsNames));
//        assertEquals(exception.getMessage(), "Can't create row of buttons. Array of names contains NULL element");
    }

    @Test
    public void createRowButtons_arrayNamesContainBlank_provideCustomErrorMessage() {
//        String[] buttonsNames = {buttonName1, buttonName2, ""};
//        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createRowButtons(buttonsNames));
//        assertEquals(exception.getMessage(), "Can't create row of button. There is no name for the button");
    }

    @Test
    public void createMainMenuButtonMarkup_success() {
//        var mainMenuButtonName = ButtonLabelKey.COMMON_RETURN_MAIN_MENU.value();
//        var mainMenuButtonMarkup = KeyboardUtils.createMainMenuButtonMarkup();
//
//        var hasMainMenuButton = mainMenuButtonMarkup.getKeyboard()
//                .stream()
//                .anyMatch(buttonRow -> buttonRow.contains(mainMenuButtonName));
//
//        assertTrue(hasMainMenuButton, "Can't find main menu button in markup");
    }

    @Test
    public void createKeyboardMarkup_baseConfigCheck_success() {
//        boolean hasButtons = true;
//        String[] buttonNames = {buttonName1, buttonName2};
//        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
//        var markup = KeyboardUtils.createKeyboardMarkup(false, rowButtons);
//
//        for (String buttonName : buttonNames) {
//            hasButtons &= markup.getKeyboard()
//                    .stream()
//                    .anyMatch(buttonRow -> buttonRow.contains(buttonName));
//        }
//
//        assertTrue(hasButtons, "Can't find some button in markup. One of them was not created");
//
//        assertTrue(markup.getSelective(), "markup.getSelective() setting was changed");
//        assertTrue(markup.getResizeKeyboard(), "markup.getResizeKeyboard() setting was changed");
//        assertFalse(markup.getOneTimeKeyboard(), "markup.getOneTimeKeyboard() setting was changed");
    }

    @Test
    public void createKeyboardMarkup_withMainMenuButton_success() {
//        var mainMenuButtonName = ButtonLabelKey.COMMON_RETURN_MAIN_MENU.value();
//        String[] buttonNames = {buttonName1, buttonName2, mainMenuButtonName};
//
//        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
//        var markup = KeyboardUtils.createKeyboardMarkup(true, rowButtons);
//
//        markup.getSelective();
//        markup.getResizeKeyboard();
//        markup.getOneTimeKeyboard();
    }

    @Test
    public void createKeyboardMarkup_keyboardRowHasNulls_provideCustomErrorMessage() {
//        String[] buttonNames = {buttonName1, buttonName2};
//        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
//        var markup = KeyboardUtils.createKeyboardMarkup(true, rowButtons);
//
//        markup.getSelective();
//        markup.getResizeKeyboard();
//        markup.getOneTimeKeyboard();
    }

    @Test
    public void createKeyboardMarkup_kulls_provideCustomErrorMessage() {
      

    }

    @ParameterizedTest
    @ValueSource(longs = {1, 5, 30})
    public void testDateRange(long daysToClone) {
        var dateOfWs = LocalDate.parse("2023-08-05");
        var startCloneDate =  dateOfWs.plusDays(1);
        var endCloneDate = dateOfWs.plusDays(daysToClone);

        var dateRange = getDateRange(startCloneDate, endCloneDate);

        var dates = dateRange.stream()
                .map(date -> new DateToWeekDay(date.toString(), date.getDayOfWeek().toString()).toString())
                .collect(Collectors.joining("\n"));

        System.out.printf("inputDate: %s\ndaysToClone: %d\nquantityClonedDays: %d\nclonedDates: \n%s\n", dateOfWs, daysToClone,dateRange.size(), dates);
    }

    public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate);
        if (Objects.isNull(endDate)) {
            List.of(startDate);
        }

        // TODO: temporary solution, for testing approach of using nextday(1 day exclude weekend),
        //  nextweek(5 days excluding weekend, nextmonth(30 days excluding weekend)
        List<LocalDate> datesInRange = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            var dayOfWeek = currentDate.getDayOfWeek();

            if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                currentDate = currentDate.plusDays(1);
                endDate = endDate.plusDays(1);
            } else {
                datesInRange.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }
        return datesInRange;
    }

    record DateToWeekDay (String date, String weekDay) {
    }
}