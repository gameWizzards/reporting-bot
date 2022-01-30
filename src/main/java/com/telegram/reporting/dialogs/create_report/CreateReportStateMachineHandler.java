package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("CreateReportStateMachineHandler")
public class CreateReportStateMachineHandler implements StateMachineHandler {

    private final Map<Long, StateMachine<CreateReportState, MessageEvent>> stateMachines = new HashMap<>();

    @Autowired
    private StateMachineFactory<CreateReportState, MessageEvent> stateMachineFactory;

    @Override
    public void handleMessage(Long chatId, Message message) {
        StateMachine<CreateReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        MessageEvent messageEvent = switch (message) {
            case CREATE_REPORT -> MessageEvent.CREATE_REPORT_EVENT;
            case USER_DATE_INPUT -> MessageEvent.USER_DATE_INPUT;
//           case CHOICE_REPORT_CATEGORY -> ?? кнопки
//           case
//           case VALID_TIME ->
//           case INVALID_TIME ->
//           case CONFIRM_ADDITIONAL_REPORT ->
//           case DECLINE_ADDITIONAL_REPORT ->
//           case CONFIRM_CREATION_FINAL_REPORT ->
//           case DECLINE_CREATION_FINAL_REPORT ->
//           case UPDATE_REPORT ->
//           case DELETE_REPORT ->
//           case ADD_NEW_USER ->
//           case CANCEL ->
//           case RETURN_TO_MAIN_MENU ->
            default -> null;
        };

        log.info("Current state = [{}]. Message = [{}] -> MessageEvent = [{}]", stateMachine.getState().getId(), message, messageEvent);
        stateMachine.sendEvent(messageEvent);
        log.info("Current state = [{}]. StateMachineId = {}", stateMachine.getState().getId(), stateMachine.getUuid());
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<CreateReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        log.info("Current state = [{}]. StateMachineId = {}", stateMachine.getState().getId(), stateMachine.getUuid());
        CreateReportState stateLog = stateMachine.getState().getId();
        MessageEvent messageEvent;
        log.info("User input = [{}]", userInput);
        CreateReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        if (CreateReportState.USER_DATE_INPUTTING.equals(currentState)) {
            messageEvent = MessageEvent.USER_DATE_INPUT;
        } else {
            messageEvent = MessageEvent.USER_TIME_INPUT;
        }

        variables.put(messageEvent.name(), userInput);
        stateMachine.sendEvent(messageEvent);
        log.info("Current state = [{}]. User input = [{}] -> MessageEvent = [{}]", stateLog, userInput, messageEvent);

    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        stateMachines.put(chatId, stateMachineFactory.getStateMachine());
        return this;
    }
}

