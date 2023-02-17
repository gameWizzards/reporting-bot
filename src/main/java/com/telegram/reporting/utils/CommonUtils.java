package com.telegram.reporting.utils;

import com.telegram.reporting.bot.command.Command;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Objects;

@Slf4j
public class CommonUtils {
    private static final String PHONE_FORMAT_REGEX = "^380[0-9]{9}";
    private CommonUtils() {
    }

    public static Long currentChatId(Update update) {
        return Objects.nonNull(update.getMessage())
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();
    }

    public static <S, E> Long currentChatId(StateContext<S, E> context) {
        return getContextVar(context, Long.class, ContextVarKey.CHAT_ID);
    }

    public static <S, E> String getContextVarAsString(StateContext<S, E> context, ContextVarKey key) {
        return getContextVar(context, String.class, key);
    }

    public static <S, E, T> T getContextVar(StateContext<S, E> context, Class<T> clazz, ContextVarKey key) {
        Object value = new Object();
        try {
            value = context.getExtendedState().getVariables().get(key);
            return clazz.cast(value);
        } catch (ClassCastException e) {
            String message = "Can't cast context var. Key: %s. Value: %s. Clazz: %s".formatted(key, value, clazz);
            throw new ClassCastException(message);
        }
    }

    public static String getMessageText(Update update) {
        if (Objects.nonNull(update.getMessage())) {
            return update.getMessage().getText().trim();
        }
        return update.getCallbackQuery().getMessage().getText().trim();
    }

    public static boolean hasMessageText(Update update) {
        if (Objects.nonNull(update.getMessage())) {
            return Objects.nonNull(update.getMessage().getText());
        }
        return Objects.nonNull(update.getCallbackQuery().getMessage().getText());
    }

    public static boolean hasContact(Update update) {
        return Objects.nonNull(update.getMessage()) && update.getMessage().hasContact();
    }

    public static String getButtonCallbackData(Update update) {
        return update.getCallbackQuery().getData();
    }

    public static boolean isInlineButton(Update update) {
        return update.hasCallbackQuery() && StringUtils.isNotBlank(update.getCallbackQuery().getData());
    }


    public static String createLogPrefix(String dialogName, Long chatId) {
        return "%s-[%s]".formatted(chatId, dialogName);
    }

    public static <S, E> String getLogPrefix(StateMachine<S, E> stateMachine) {
        return (String) stateMachine.getExtendedState().getVariables().getOrDefault(ContextVarKey.LOG_PREFIX, getDefaultPrefix(stateMachine));
    }

    private static <S, E> String getDefaultPrefix(StateMachine<S, E> stateMachine) {
        return "Undefined prefix Dialog= %s".formatted(stateMachine.getInitialState()
                .getId()
                .getClass()
                .getSimpleName()
                .replaceAll("State", ""));
    }

    public static boolean isTelegramCommand(Update update) {
        String message = CommonUtils.getMessageText(update);
        return message.startsWith(Command.COMMAND_PREFIX);
    }

    public static boolean isDynamicOrdinalInlineButton(String buttonCallbackData) {
        try {
            Long.parseLong(buttonCallbackData);
            return true;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Received unappropriated callback data when try to check for Ordinal button. Callback data=" + buttonCallbackData);
        }
    }

    public static boolean hasAccess(DialogHandler handler, User user) {
         return !Collections.disjoint(handler.roleAccessibility(), user.getRoles());
    }

    public static String normalizePhoneNumber(String phone) {
        if (StringUtils.isBlank(phone)) {
            return "";
        }

        String specSymbolsRegex = "[()\\-+ ]";

        String clearedInput = phone.replaceAll(specSymbolsRegex, "");
        return clearedInput.startsWith("0")
                ? "38" + clearedInput
                : clearedInput;

    }

    public static boolean isCorrectPhoneFormat(String phone) {
        return phone.matches(PHONE_FORMAT_REGEX);
    }
}
