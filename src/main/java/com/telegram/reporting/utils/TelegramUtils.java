package com.telegram.reporting.utils;

import org.springframework.statemachine.StateContext;
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
    public static Long currentChatId(Update update) {
        return update.getMessage().getChatId();
    }

    public static String currentChatId(StateContext context) {
        return String.valueOf(context.getExtendedState().getVariables().get("chat_id"));
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
