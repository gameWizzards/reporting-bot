package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.ContextVariable;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUtils {

    public static String COMMAND_PREFIX = "/";

    public static Long currentChatId(Update update) {
        return update.getMessage().getChatId();
    }

    public static String currentChatId(StateContext context) {
        return String.valueOf(context.getExtendedState().getVariables().get(ContextVariable.CHAT_ID));
    }

    public static String getMessage(Update update) {
        return update.getMessage().getText().trim();
    }

    public static String getCommandIdentifier(String message) {
        return message.split(" ")[0].toLowerCase();
    }

    public static String createLogPrefix(String dialogName, String telegramNickname) {
        return String.format("%s-[%s]", telegramNickname, dialogName);
    }

    public static <S, E> String getLogPrefix(StateMachine<S, E> stateMachine) {
        return (String) stateMachine.getExtendedState().getVariables().getOrDefault(ContextVariable.LOG_PREFIX, getDefaultPrefix(stateMachine));
    }

    private static <S, E> String getDefaultPrefix(StateMachine<S,E> stateMachine) {
        return "Undefined prefix Dialog=" + stateMachine.getInitialState()
                                                            .getId()
                                                            .getClass()
                                                            .getSimpleName()
                                                            .replaceAll("State", "");
    }
}
