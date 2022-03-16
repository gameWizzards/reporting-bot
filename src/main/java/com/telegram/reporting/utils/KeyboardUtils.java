package com.telegram.reporting.utils;

import com.telegram.reporting.messages.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KeyboardUtils {

    public static KeyboardRow createButton(String name) {
        Objects.requireNonNull(name, "Can't create button without name");
        return createRowButtons(name);
    }

    public static KeyboardRow createRowButtons(String... names) {
        Objects.requireNonNull(names, "Can't create row of buttons without names");
        KeyboardRow button = new KeyboardRow();
        button.addAll(Arrays.asList(names));
        return button;
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(boolean addMainMenuButton, KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        if (addMainMenuButton) {
            KeyboardRow mainMenuButton = createButton(Message.MAIN_MENU.text());
            keyboardRows.add(mainMenuButton);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
// TODO implement configured keys generator

//    public static ReplyKeyboardMarkup createKeyboardMarkup(int buttonsInRow, String... nameKeys) {
//        Objects.requireNonNull(nameKeys, "Can't create keyboard markup without keyboard rows");
//        if (buttonsInRow < 1) {
//            buttonsInRow = 1;
//        } else if (buttonsInRow > nameKeys.length) {
//            buttonsInRow = nameKeys.length;
//        }
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(false);
//        replyKeyboardMarkup.setKeyboard(prepareKeyboardsRows(buttonsInRow, nameKeys));
//        return replyKeyboardMarkup;
//    }
//
//    private static List<KeyboardRow> prepareKeyboardsRows(int buttonsInRow, String... nameKeys) {
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        int fullCycle = nameKeys.length / buttonsInRow;
//        int buttonsOnLastRow = nameKeys.length % buttonsInRow;
//
//        List<String> keys;
//        KeyboardRow keyboardRow;
//
//        for (int i = 1; i<=fullCycle; i++){
//            keyboardRow = new KeyboardRow();
//            keys = new ArrayList<>();
//            for (int j = 1; j <= buttonsInRow; j++) {
//                keys.add(nameKeys[j]);
//            }
//            keyboardRow.addAll(keys);
//            keyboardRows.add(keyboardRow);
//        }
//
//        if (buttonsOnLastRow > 0) {
//            keyboardRow = new KeyboardRow();
//            keys = new ArrayList<>();
//            for (int j = 1; j <= buttonsInRow; j++) {
//                keys.add(nameKeys[j]);
//            }
//            keyboardRow.addAll(keys);
//            keyboardRows.add(keyboardRow);
//        }
//        return keyboardRows;
//    }
}
