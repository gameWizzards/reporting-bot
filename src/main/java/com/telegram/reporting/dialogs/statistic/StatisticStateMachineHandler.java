package com.telegram.reporting.dialogs.statistic;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.StateMachineHandler;
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


    public StatisticStateMachineHandler(StateMachineFactory<StatisticState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<StatisticState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case STATISTIC_START_DIALOG ->  MessageEvent.RUN_STATISTIC_DIALOG;
            case PREVIOUS_MONTH_STATISTIC ->  MessageEvent.SHOW_PREVIOUS_MONTH_STATISTIC;

            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<StatisticState, MessageEvent> stateMachine = stateMachines.get(chatId);
        StatisticState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        MessageEvent messageEvent = switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVariable.DATE, userInput);
                yield MessageEvent.VALIDATE_USER_DATE_INPUT;
            }

            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);

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

