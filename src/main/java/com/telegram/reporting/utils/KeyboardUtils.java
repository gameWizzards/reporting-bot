package com.telegram.reporting.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyboardUtils {

    public static KeyboardRow createButton(String name) {
        Objects.requireNonNull(name, "Can't create button without name");
        KeyboardRow button = new KeyboardRow();
        button.add(name);
        return button;
    }

    public static KeyboardRow createRowButtons(String... names) {
        KeyboardRow button = new KeyboardRow();
        button.addAll(Arrays.asList(names));
        return button;
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(Arrays.asList(rows));
        return replyKeyboardMarkup;
    }
}
