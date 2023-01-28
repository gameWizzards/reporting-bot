package com.telegram.reporting.dialogs.general_dialogs.statistic;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DefaultDialogListener;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<StatisticState, StatisticEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<StatisticState, StatisticEvent> stateMachineFactory;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18NMessageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<StatisticState, StatisticEvent> stateMachine = stateMachines.get(chatId);
        StatisticEvent messageEvent = switch (buttonLabelKey) {
            case GS_START_DIALOG -> StatisticEvent.RUN_STATISTIC_DIALOG;
            case GS_PREVIOUS_MONTH_STATISTIC -> StatisticEvent.SHOW_PREVIOUS_MONTH_STATISTIC;
            case GS_CURRENT_MONTH_STATISTIC -> StatisticEvent.SHOW_CURRENT_MONTH_STATISTIC;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Statistic] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_USER_INPUT_INSTEAD_BTN));
    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<StatisticState, StatisticEvent> stateMachine = stateMachineFactory.getStateMachine();

        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("Statistic", chatId));

        stateMachine.addStateListener(new DefaultDialogListener<>(stateMachine));

        stateMachines.put(chatId, stateMachine);

        eventPublisher.publishEvent(new ClearStatisticCacheEvent(chatId));
        return this;
    }

    @Override
    public void removeDialogData(Long chatId) {
        stateMachines.get(chatId).getExtendedState().getVariables().clear();
    }

    @Override
    public ButtonLabelKey startDialogButtonKey() {
        return ButtonLabelKey.GS_START_DIALOG;
    }
}

