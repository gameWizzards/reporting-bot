package com.telegram.reporting.dialogs.manager.add_employee;

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
@Component("AddEmployeeStateMachineHandler")
public class AddEmployeeStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<AddEmployeeState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<AddEmployeeState, MessageEvent>> stateMachines;


    public AddEmployeeStateMachineHandler(StateMachineFactory<AddEmployeeState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<AddEmployeeState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case ADD_EMPLOYEE_START_DIALOG -> MessageEvent.RUN_ADD_EMPLOYEE_DIALOG;
            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<AddEmployeeState, MessageEvent> stateMachine = stateMachines.get(chatId);
        AddEmployeeState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        MessageEvent messageEvent = switch (currentState) {
            case USER_PHONE_INPUTTING -> {
                variables.put(ContextVariable.PHONE, userInput);
                yield MessageEvent.VALIDATE_PHONE_INPUT;
            }
            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<AddEmployeeState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("AddEmployee", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}