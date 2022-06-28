package com.telegram.reporting.dialogs.edit_dialog;

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
@Component("EditReportStateMachineHandler")
public class EditReportStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<EditReportState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<EditReportState, MessageEvent>> stateMachines;


    public EditReportStateMachineHandler(StateMachineFactory<EditReportState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<EditReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case EDIT_REPORT_START_DIALOG -> MessageEvent.RUN_EDIT_REPORT_DIALOG;
            case INPUT_NEW_DATE -> MessageEvent.RETURN_TO_USER_DATE_INPUTTING;
            case SPEND_TIME -> MessageEvent.CHOOSE_EDIT_SPEND_TIME;
            case CATEGORY -> MessageEvent.CHOOSE_EDIT_CATEGORY;
            case NOTE -> MessageEvent.CHOOSE_EDIT_NOTE;
            case REPORT_CATEGORY_ON_STORAGE,
                    REPORT_CATEGORY_ON_ORDER,
                    REPORT_CATEGORY_ON_OFFICE,
                    REPORT_CATEGORY_ON_COORDINATION -> MessageEvent.HANDLE_USER_CHANGE_CATEGORY;

            case CONFIRM_EDIT_ADDITIONAL_DATA -> MessageEvent.CONFIRM_EDIT_ADDITIONAL_DATA;
            case DECLINE_EDIT_ADDITIONAL_DATA -> MessageEvent.DECLINE_EDIT_ADDITIONAL_DATA;
            case APPLY_DATA_CHANGES -> MessageEvent.CONFIRM_EDIT_DATA;
            case CANCEL -> MessageEvent.DECLINE_EDIT_DATA;
            case YES -> MessageEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD;
            case NO -> MessageEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD;
            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EditReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        EditReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        MessageEvent messageEvent = switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVariable.DATE, userInput);
                yield MessageEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVariable.TIME_RECORD_CHOICE, userInput);
                yield MessageEvent.CHOOSE_TIME_RECORD;
            }
            case USER_CHANGE_SPEND_TIME -> {
                variables.put(ContextVariable.REPORT_TIME, userInput);
                yield MessageEvent.HANDLE_USER_CHANGE_SPEND_TIME;
            }
            case USER_CHANGE_NOTE -> {
                variables.put(ContextVariable.REPORT_NOTE, userInput);
                yield MessageEvent.HANDLE_USER_CHANGE_NOTE;
            }
            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);

    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<EditReportState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("Edit_report", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

