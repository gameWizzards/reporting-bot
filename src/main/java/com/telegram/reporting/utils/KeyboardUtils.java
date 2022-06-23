package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.ButtonValue;
import org.apache.commons.lang3.Validate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class KeyboardUtils {

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

    public static ReplyKeyboardMarkup createMainMenuButtonMarkup() {
        return createKeyboardMarkup(true, new KeyboardRow());
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(boolean addMainMenuButton, KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        if (addMainMenuButton) {
            KeyboardRow mainMenuButton = createButton(ButtonValue.MAIN_MENU.text());
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
        KeyboardRow firstRow = KeyboardUtils.createButton(ButtonValue.CREATE_REPORT_START_DIALOG.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(ButtonValue.UPDATE_REPORT_START_DIALOG.text(), ButtonValue.DELETE_REPORT_START_DIALOG.text());
        sendMessage.setReplyMarkup(KeyboardUtils.createKeyboardMarkup(false, firstRow, secondRow));

        return sendMessage;
    }
}
