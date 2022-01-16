package com.telegram.reporting.utils;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Utils class for Commands.
 */
public class TelegramUtils {

    public static String COMMAND_PREFIX = "/";

    /**
     * Get chatId from {@link Update} object.
     *
     * @param update provided {@link Update}
     * @return chatID from the provided {@link Update} object.
     */
    public static Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    /**
     * Get text of the message from {@link Update} object.
     *
     * @param update provided {@link Update}
     * @return the text of the message from the provided {@link Update} object.
     */
    public static String getMessage(Update update) {
        return update.getMessage().getText().trim();
    }
}