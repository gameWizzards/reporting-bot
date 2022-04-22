package com.telegram.reporting.utils;

import com.telegram.reporting.messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public static ReplyKeyboardMarkup createMainMenuButtonMarkup() {
        return createKeyboardMarkup(true, new KeyboardRow());
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

    public static SendMessage createRootMenuMessage(String chatId) {
        Objects.requireNonNull(chatId, "Can't create root menu buttons! ChatId is required");
        final String startFlowMessage = """
                Окей.
                Выбери диалог.
                """;

        SendMessage sendMessage = new SendMessage(chatId, startFlowMessage);
        KeyboardRow firstRow = KeyboardUtils.createButton(Message.CREATE_REPORT_START_MESSAGE.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(Message.UPDATE_REPORT_START_MESSAGE.text(), Message.DELETE_REPORT_START_MESSAGE.text());
        sendMessage.setReplyMarkup(KeyboardUtils.createKeyboardMarkup(false, firstRow, secondRow));

        return sendMessage;
    }
}
