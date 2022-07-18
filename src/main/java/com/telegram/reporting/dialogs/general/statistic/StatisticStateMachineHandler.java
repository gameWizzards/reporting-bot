package com.telegram.reporting.dialogs.general.statistic;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("StatisticStateMachineHandler")
public class StatisticStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<StatisticState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<StatisticState, MessageEvent>> stateMachines;
    private final SendBotMessageService sendBotMessageService;


    public StatisticStateMachineHandler(StateMachineFactory<StatisticState, MessageEvent> stateMachineFactory,
                                        SendBotMessageService sendBotMessageService) {
        this.stateMachineFactory = stateMachineFactory;
        this.sendBotMessageService = sendBotMessageService;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<StatisticState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case STATISTIC_START_DIALOG -> MessageEvent.RUN_STATISTIC_DIALOG;
            case PREVIOUS_MONTH_STATISTIC -> MessageEvent.SHOW_PREVIOUS_MONTH_STATISTIC;

            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        sendBotMessageService.sendMessage(chatId, "Используй кнопки. Не напрягайся печатать буквы...");
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<StatisticState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("Statistic", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

