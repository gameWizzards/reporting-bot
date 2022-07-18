package com.telegram.reporting.dialogs.manager.employee_statistic;

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
@Component("EmployeeStatisticStateMachineHandler")
public class EmployeeStatisticStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<EmployeeStatisticState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<EmployeeStatisticState, MessageEvent>> stateMachines;


    public EmployeeStatisticStateMachineHandler(StateMachineFactory<EmployeeStatisticState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<EmployeeStatisticState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case EMPLOYEE_STATISTIC_START_DIALOG -> MessageEvent.RUN_EMPLOYEE_STATISTIC_DIALOG;
            case INPUT_NEW_DATE -> MessageEvent.RETURN_TO_USER_DATE_INPUTTING;
            case YES -> MessageEvent.CONFIRM_CHANGE_LOCK_REPORT_STATUS;
            case NO -> MessageEvent.DECLINE_CHANGE_LOCK_REPORT_STATUS;
            case LOCK_EDIT_REPORT_DATA,
                    UNLOCK_EDIT_REPORT_DATA -> MessageEvent.HANDLE_LOCK_REPORT_DATA_TO_EDIT;
            case CHOICE_ANOTHER_EMPLOYEE,
                    CANCEL -> MessageEvent.CHOOSE_ANOTHER_EMPLOYEE;
            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EmployeeStatisticState, MessageEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatisticState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        MessageEvent messageEvent = switch (currentState) {
            case USER_MONTH_INPUTTING -> {
                variables.put(ContextVariable.DATE, userInput);
                yield MessageEvent.VALIDATE_USER_MONTH_INPUT;
            }
            case USER_EMPLOYEE_CHOOSING -> {
                variables.put(ContextVariable.EMPLOYEE_ORDINAL, userInput);
                yield MessageEvent.CHOOSE_EMPLOYEE;
            }
            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<EmployeeStatisticState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("EmployeeStatistic", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

