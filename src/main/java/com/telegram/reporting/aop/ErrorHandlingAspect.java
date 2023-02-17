package com.telegram.reporting.aop;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.exception.TelegramUserDeletedException;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ErrorHandlingAspect {
    private final SendBotMessageService sendBotMessageService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;
    private final MessageSource messageSource;

    @AfterThrowing(
            pointcut = "execution(* com.telegram.reporting.dialogs..*(org.springframework.statemachine.StateContext))",
            throwing = "e")
    public void doStateMachineExceptionLogging(JoinPoint jp, Exception e) {

        StateContext context = Stream.of(jp.getArgs())
                .filter(arg -> arg instanceof StateContext<?, ?>)
                .map(DefaultStateContext.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find StateContext in joinPoint args. Class: %s. Method: %s"
                        .formatted(jp.getTarget().getClass().getSimpleName(), jp.getSignature().toLongString())));

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

    @AfterThrowing(pointcut = "execution(* com.telegram.reporting.dialogs.DialogProcessor.handleButtonClick(..))",
            throwing = "e")
    public void onMissClickButtonExceptionMessage(ButtonToEventMappingException e) {
        long chatId = e.getChatId();

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_CLICK_ANOTHER_DIALOG_BUTTON));
    }

    @AfterThrowing(pointcut = "this(com.telegram.reporting.service.RuntimeDialogManager)", throwing = "e")
    public void onUserStatusDeletedExceptionMessage(TelegramUserDeletedException e) {
        long chatId = e.getChatId();

        String deletedUserErrMessage = messageSource.getMessage(
                MessageKey.COMMON_WARNING_USER_STATUS_DELETED.value(),
                null,
                I18nPropsResolver.DEFAULT_LOCALE);

        sendBotMessageService.sendMessage(chatId, deletedUserErrMessage);
    }
}
