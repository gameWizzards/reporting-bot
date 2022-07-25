package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.repository.dto.Ordinal;
import org.apache.commons.lang3.Validate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class KeyboardUtils {
    public static final List<ButtonValue> MANAGER_MENU_BUTTONS = List.of(ButtonValue.RETURN_MANAGER_MENU, ButtonValue.RETURN_MAIN_MENU);
    public static final List<ButtonValue> ADMIN_MENU_BUTTONS = List.of(ButtonValue.RETURN_ADMIN_MENU, ButtonValue.RETURN_MAIN_MENU);

    private KeyboardUtils() {
    }

    public static KeyboardRow createButton(String name) {
        Validate.notBlank(name, "Can't create button without name");
        return createRowButtons(name);
    }

    public static KeyboardRow createRowButtons(String... names) {
        Validate.noNullElements(names, "Can't create row of buttons. Array of names contains NULL element");
        Stream.of(names).forEach(name -> Validate.notBlank(name, "Can't create row of button. There is no name for the button"));
        KeyboardRow button = new KeyboardRow();
        button.addAll(Arrays.asList(names));
        return button;
    }

    public static KeyboardRow[] createButtonsWithRows(String[] buttonNames, int buttonsInRow) {
        return createButtonsWithRows(Arrays.asList(buttonNames), buttonsInRow);
    }

    public static KeyboardRow[] createButtonsWithRows(List<String> buttonNames, int buttonsInRow) {
        Validate.notEmpty(buttonNames, "Can't create buttons with rows. List of buttonNames empty or NULL");
        Validate.noNullElements(buttonNames, "Can't create buttons with rows. List of buttonNames contains NULL element");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<String> listBN = new ArrayList<>(buttonNames);

        if (listBN.size() < buttonsInRow || buttonsInRow < 1) {
            buttonsInRow = listBN.size();
        }

        int cycle = listBN.size() / buttonsInRow;
        int lastRowButtons = listBN.size() % buttonsInRow;

        for (int i = 0; i < cycle; i++) {
            List<String> subBNs = new ArrayList<>(listBN.subList(0, buttonsInRow));
            KeyboardRow button = KeyboardUtils.createRowButtons(subBNs.toArray(String[]::new));
            subBNs.forEach(listBN::remove);

            keyboardRows.add(button);
        }

        if (lastRowButtons != 0) {
            keyboardRows.add(KeyboardUtils.createRowButtons(listBN.toArray(String[]::new)));
        }

        return keyboardRows.toArray(KeyboardRow[]::new);
    }

    public static ReplyKeyboardMarkup createMainMenuButtonMarkup() {
        return createKeyboardMarkup(true, new KeyboardRow());
    }

    public static ReplyKeyboardMarkup createMenuButtonMarkup(List<ButtonValue> menuButtons) {
        return createKeyboardMarkup(menuButtons, new KeyboardRow());
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(boolean addMainMenuButton, KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        if (addMainMenuButton) {
            KeyboardRow mainMenuButton = createButton(ButtonValue.RETURN_MAIN_MENU.text());
            keyboardRows.add(mainMenuButton);
        }
        return createReplyKeyboardMarkup(keyboardRows);
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(List<ButtonValue> menuButtons, KeyboardRow... rows) {
        Validate.notEmpty(menuButtons, "Can't create menu buttons. List menuButtons empty or NULL");
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        if (!menuButtons.isEmpty()) {
            List<String> buttonNames = menuButtons.stream()
                    .map(ButtonValue::text)
                    .toList();
            keyboardRows.addAll(Arrays.asList(createButtonsWithRows(buttonNames, 2)));
        }
        return createReplyKeyboardMarkup(keyboardRows);
    }

    public static List<String> getAvailableCategoryButtons(String timeRecordsJson) {
        return ButtonValue.categoryButtons().stream()
                .map(ButtonValue::text)
                .filter(Predicate.not(Optional.ofNullable(timeRecordsJson).orElse("")::contains))
                .toList();
    }

    public static String[] getButtonsByOrdinalNumber(List<? extends Ordinal> ordinals) {
        Validate.notEmpty(ordinals, "Can't create buttons for ordinals. List of ordinals is empty or NULL");
        Validate.noNullElements(ordinals, "Object must contain ordinal number to creating buttons for them.");

        List<String> buttons = new ArrayList<>(ordinals.size());
        ordinals.forEach(ord -> buttons.add(String.valueOf(ord.getOrdinal())));
        return buttons.toArray(String[]::new);
    }

    private static ReplyKeyboardMarkup createReplyKeyboardMarkup(List<KeyboardRow> keyboardRows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
