package com.telegram.reporting.aop;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ErrorHandlingAspect {
    private final SendBotMessageService sendBotMessageService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;

    @AfterThrowing(
            pointcut = "execution(* com.telegram.reporting.*.* (..)) && args(context,..)",
            throwing = "e")
    public <S, E> void doExceptionLogging(Exception e, StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String logPrefix = CommonUtils.getContextVarAsString(context, ContextVarKey.LOG_PREFIX);
        String exceptLogs = "reactor";

        String contextVarValues = context.getExtendedState().getVariables().keySet().stream()
                .map(key -> key + "=%s".formatted(context.getExtendedState().getVariables().get(key)))
                .collect(Collectors.joining(", ", "{", "}"));

        StackTraceElement[] stackTrace = Arrays.stream(e.getStackTrace()).parallel()
                .filter(ste -> !ste.getClassName().contains(exceptLogs))
                .toArray(StackTraceElement[]::new);

        e.setStackTrace(stackTrace);

        log.error(logPrefix + e.getMessage(), e);
        log.error("{} Context variables = {}", logPrefix, contextVarValues);

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_SOMETHING_GOING_WRONG));

        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createMainMenuInlineMarkup(chatId));
    }

    @AfterThrowing(pointcut = "this(com.telegram.reporting.dialogs.DialogProcessor)", throwing = "e")
    public void onMissClickButtonExceptionMessage(ButtonToEventMappingException e) {
        long chatId = e.getChatId();

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_CLICK_ANOTHER_DIALOG_BUTTON));
    }
}
