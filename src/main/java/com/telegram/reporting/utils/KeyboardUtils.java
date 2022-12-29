package com.telegram.reporting.utils;

import org.apache.commons.lang3.Validate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyboardUtils {

    private KeyboardUtils() {
    }

    public static KeyboardRow createSimpleButton(String name) {
        Validate.notBlank(name, "Can't create button without name");
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(name));
        return keyboardRow;
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        return createReplyKeyboardMarkup(keyboardRows);
    }

    public static List<List<InlineKeyboardButton>> separateInlineButtonsToRows(List<InlineKeyboardButton> buttons, int buttonsInRow) {
        Validate.notEmpty(buttons, "Can't create buttons with rows. List of buttonNames empty or NULL");
        Validate.noNullElements(buttons, "Can't create buttons with rows. List of buttonNames contains NULL element");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonToSeparate = new ArrayList<>(buttons);

        if (buttonToSeparate.size() < buttonsInRow || buttonsInRow < 1) {
            buttonsInRow = buttonToSeparate.size();
        }

        int cycle = buttonToSeparate.size() / buttonsInRow;
        int lastRowButtons = buttonToSeparate.size() % buttonsInRow;

        for (int i = 0; i < cycle; i++) {
            List<InlineKeyboardButton> rowButtons = new ArrayList<>(buttonToSeparate.subList(0, buttonsInRow));
            rowButtons.forEach(buttonToSeparate::remove);
            rows.add(rowButtons);
        }

        if (lastRowButtons != 0) {
            rows.add(buttonToSeparate);
        }

        return rows;
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
