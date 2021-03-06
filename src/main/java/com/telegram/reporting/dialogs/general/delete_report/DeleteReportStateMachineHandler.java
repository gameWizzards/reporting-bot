package com.telegram.reporting.dialogs.general.delete_report;

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
@Component("DeleteReportStateMachineHandler")
public class DeleteReportStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<DeleteReportState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<DeleteReportState, MessageEvent>> stateMachines;


    public DeleteReportStateMachineHandler(StateMachineFactory<DeleteReportState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<DeleteReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        MessageEvent messageEvent = switch (buttonValue) {
            case DELETE_REPORT_START_DIALOG -> MessageEvent.RUN_DELETE_REPORT_DIALOG;
            case INPUT_NEW_DATE -> MessageEvent.RETURN_TO_USER_DATE_INPUTTING;
            case CONFIRM_DELETE_TIME_RECORD -> MessageEvent.CONFIRM_DELETE_TIME_RECORD;
            case CANCEL -> MessageEvent.DECLINE_DELETE_TIME_RECORD;
            default -> null;
        };
        stateMachine.getExtendedState().getVariables().put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<DeleteReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        MessageEvent messageEvent = null;
        DeleteReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVariable.DATE, userInput);
                messageEvent = MessageEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVariable.TIME_RECORD_CHOICE, userInput);
                messageEvent = MessageEvent.CHOOSE_TIME_RECORD;
            }
        }

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);

    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<DeleteReportState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("Delete_report", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

