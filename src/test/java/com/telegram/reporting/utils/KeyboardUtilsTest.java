package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.ButtonValue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class KeyboardUtilsTest {
    private final String buttonName1 = "Some button1";
    private final String buttonName2 = "Some button2";

    @Test
    public void createButton_validButtonName_success() {
        var button = KeyboardUtils.createButton(buttonName1);
        assertTrue(button.contains(buttonName1), "Can't find button with assigning name: %s".formatted(buttonName1));
    }

    @Test
    public void createButton_inputEmpty_provideCustomErrorMessage() {
        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createButton(""));
        assertEquals(exception.getMessage(), "Can't create button without name");
    }

    @Test
    public void createButton_inputNull_provideCustomErrorMessage() {
        var exception = assertThrows(NullPointerException.class, () -> KeyboardUtils.createButton(null));
        assertEquals(exception.getMessage(), "Can't create button without name");
    }

    @Test
    public void createRowButtons_validButtonNames_success() {
        String[] buttonNames = {buttonName1, buttonName2};
        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
        assertEquals(buttonNames.length, rowButtons.size(), "Expected quantity of buttons don't create.");
        assertTrue(rowButtons.contains(buttonName1), "Can't find button with assigning name: %s".formatted(buttonName1));
        assertTrue(rowButtons.contains(buttonName2), "Can't find button with assigning name: %s".formatted(buttonName2));
    }

    @Test
    public void createRowButtons_arrayNamesContainNull_provideCustomErrorMessage() {
        String[] buttonsNames = {buttonName1, buttonName2, null};
        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createRowButtons(buttonsNames));
        assertEquals(exception.getMessage(), "Can't create row of buttons. Array of names contains NULL element");
    }

    @Test
    public void createRowButtons_arrayNamesContainBlank_provideCustomErrorMessage() {
        String[] buttonsNames = {buttonName1, buttonName2, ""};
        var exception = assertThrows(IllegalArgumentException.class, () -> KeyboardUtils.createRowButtons(buttonsNames));
        assertEquals(exception.getMessage(), "Can't create row of button. There is no name for the button");
    }

    @Test
    public void createMainMenuButtonMarkup_success() {
        var mainMenuButtonName = ButtonValue.MAIN_MENU.text();
        var mainMenuButtonMarkup = KeyboardUtils.createMainMenuButtonMarkup();

        var hasMainMenuButton = mainMenuButtonMarkup.getKeyboard()
                .stream()
                .anyMatch(buttonRow -> buttonRow.contains(mainMenuButtonName));

        assertTrue(hasMainMenuButton, "Can't find main menu button in markup");
    }

    @Test
    public void createKeyboardMarkup_baseConfigCheck_success() {
        boolean hasButtons = true;
        String[] buttonNames = {buttonName1, buttonName2};
        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
        var markup = KeyboardUtils.createKeyboardMarkup(false, rowButtons);

        for (String buttonName : buttonNames) {
            hasButtons &= markup.getKeyboard()
                    .stream()
                    .anyMatch(buttonRow -> buttonRow.contains(buttonName));
        }

        assertTrue(hasButtons, "Can't find some button in markup. One of them was not created");

        assertTrue(markup.getSelective(), "markup.getSelective() setting was changed");
        assertTrue(markup.getResizeKeyboard(), "markup.getResizeKeyboard() setting was changed");
        assertFalse(markup.getOneTimeKeyboard(), "markup.getOneTimeKeyboard() setting was changed");
    }

    @Test
    public void createKeyboardMarkup_withMainMenuButton_success() {
        var mainMenuButtonName = ButtonValue.MAIN_MENU.text();
        String[] buttonNames = {buttonName1, buttonName2, mainMenuButtonName};

        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
        var markup = KeyboardUtils.createKeyboardMarkup(true, rowButtons);

        markup.getSelective();
        markup.getResizeKeyboard();
        markup.getOneTimeKeyboard();
    }

    @Test
    public void createKeyboardMarkup_keyboardRowHasNulls_provideCustomErrorMessage() {
        String[] buttonNames = {buttonName1, buttonName2};
        var rowButtons = KeyboardUtils.createRowButtons(buttonNames);
        var markup = KeyboardUtils.createKeyboardMarkup(true, rowButtons);

        markup.getSelective();
        markup.getResizeKeyboard();
        markup.getOneTimeKeyboard();
    }

    @Test
    public void createKeyboardMarkup_kulls_provideCustomErrorMessage() {

//        List<String> first = new ArrayList<>(List.of("first", "second", "third", "fourth", "fifth"));
        List<String> first = new ArrayList<>(List.of("first"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int buttonsInRow = 2;
        int cycle = first.size() / 2;
        int lastRowButtons = first.size() % buttonsInRow;

        for (int i = 0; i < cycle; i++) {
            List<String> sub = new ArrayList<>(first.subList(0, buttonsInRow));
            KeyboardRow button = KeyboardUtils.createRowButtons(sub.toArray(new String[0]));
            sub.forEach(first::remove);

            keyboardRows.add(button);
        }
        if (lastRowButtons != 0) {
            keyboardRows.add(KeyboardUtils.createRowButtons(first.toArray(new String[0])));
        }

        KeyboardUtils.createKeyboardMarkup(true, keyboardRows.toArray(new KeyboardRow[0]));
    }
}