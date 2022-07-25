package com.telegram.reporting.dialogs.manager.employee_status;

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
@Component("EmployeeStatusStateMachineHandler")
public class EmployeeStatusStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<EmployeeStatusState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<EmployeeStatusState, MessageEvent>> stateMachines;


    public EmployeeStatusStateMachineHandler(StateMachineFactory<EmployeeStatusState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<EmployeeStatusState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case EMPLOYEE_STATUS_START_DIALOG -> MessageEvent.RUN_EMPLOYEE_STATUS_DIALOG;
            case USER_STATUS_ACTIVE,
                    USER_STATUS_DELETED -> MessageEvent.HANDLE_USER_CHOICE_LIST;
            case CHOICE_ANOTHER_LIST_EMPLOYEES -> MessageEvent.RETURN_TO_LIST_EMPLOYEES_CHOICE;
            case CHANGE_EMPLOYEE_STATUS -> MessageEvent.HANDLE_EDIT_STATUS_CHOICE;
            case CHANGE_EMPLOYEE_ROLE -> MessageEvent.HANDLE_EDIT_ROLE_CHOICE;
            case ACTIVATE_EMPLOYEE,
                    DELETE_EMPLOYEE -> MessageEvent.HANDLE_EMPLOYEE_STATUS_CHANGE;
            case ADD_MANAGER_ROLE,
                    REMOVE_MANAGER_ROLE -> MessageEvent.HANDLE_EMPLOYEE_ROLE_CHANGE;
            case CANCEL -> MessageEvent.DECLINE_EMPLOYEE_DATA_CHANGE;

            case YES -> MessageEvent.CONFIRM_EMPLOYEE_ADDITIONAL_DATA_CHANGE;
            case NO -> MessageEvent.DECLINE_EMPLOYEE_ADDITIONAL_DATA_CHANGE;
            case CHOICE_ANOTHER_EMPLOYEE -> MessageEvent.CHOOSE_ANOTHER_EMPLOYEE;
            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EmployeeStatusState, MessageEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatusState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        MessageEvent messageEvent = switch (currentState) {
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
        StateMachine<EmployeeStatusState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("EmployeeStatistic", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}
