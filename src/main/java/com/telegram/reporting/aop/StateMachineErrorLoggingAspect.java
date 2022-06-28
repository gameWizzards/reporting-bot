package com.telegram.reporting.aop;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class StateMachineErrorLoggingAspect {

    private final SendBotMessageService sendBotMessageService;

    public StateMachineErrorLoggingAspect(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Pointcut("execution(* com.telegram.reporting.service.*.* (..))")
    public void servicePackagePointcut() {
    }

    @AfterThrowing(
            pointcut = "servicePackagePointcut() && args(context,..)",
            throwing = "e")
    public <S, E> void doExceptionLogging(Exception e, StateContext<S, E> context) {
        String exceptLogs = "reactor";
        String logPrefix = TelegramUtils.getContextVariableValue(context, ContextVariable.LOG_PREFIX);

        String contextVarValues = context.getExtendedState().getVariables().keySet().stream()
                .map(key -> key + "=" + context.getExtendedState().getVariables().get(key))
                .collect(Collectors.joining(", ", "{", "}"));

        StackTraceElement[] stackTrace = Arrays.stream(e.getStackTrace()).parallel()
                .filter(ste -> !ste.getClassName().contains(exceptLogs))
                .toArray(StackTraceElement[]::new);

        e.setStackTrace(stackTrace);

        log.error(logPrefix + e.getMessage(), e);
        log.error("{} Context variables = {}", logPrefix, contextVarValues);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), "Упс) Что-то пошло не так, попробуй начать сначала");
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }
}
