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

    public static <S, E> String currentChatId(StateContext<S, E> context) {
        return String.valueOf(context.getExtendedState().getVariables().get(ContextVariable.CHAT_ID));
    }

    public static <S, E> String getContextVariableValue(StateContext<S, E> context, ContextVariable variable) {
        return (String) context.getExtendedState().getVariables().get(variable);
    }

    public static String getMessage(Update update) {
        return update.getMessage().getText().trim();
    }

    public static String getCommandIdentifier(String message) {
        return message.split(" ")[0].toLowerCase();
    }

    public static String createLogPrefix(String dialogName, Long chatId) {
        return String.format("%s-[%s]", chatId.toString(), dialogName);
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
