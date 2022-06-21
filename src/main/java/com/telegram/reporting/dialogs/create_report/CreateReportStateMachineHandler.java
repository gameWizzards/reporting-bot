package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("CreateReportStateMachineHandler")
public class CreateReportStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<CreateReportState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<CreateReportState, MessageEvent>> stateMachines;

    public CreateReportStateMachineHandler(StateMachineFactory<CreateReportState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, Message message) {
        StateMachine<CreateReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        MessageEvent messageEvent = switch (message) {
            case CREATE_REPORT_START_MESSAGE -> MessageEvent.CREATE_REPORT_EVENT;
            case REPORT_CATEGORY_ON_STORAGE,
                    REPORT_CATEGORY_ON_ORDER,
                    REPORT_CATEGORY_ON_OFFICE,
                    REPORT_CATEGORY_ON_COORDINATION -> MessageEvent.CHOICE_REPORT_CATEGORY;
            case CONFIRM_ADDITIONAL_REPORT -> MessageEvent.CONFIRM_ADDITIONAL_REPORT;
            case DECLINE_ADDITIONAL_REPORT -> MessageEvent.DECLINE_ADDITIONAL_REPORT;
            case CONFIRM_CREATION_FINAL_REPORT -> MessageEvent.CONFIRM_CREATION_FINAL_REPORT;
            case DECLINE_CREATION_FINAL_REPORT -> MessageEvent.DECLINE_CREATION_FINAL_REPORT;
            case SKIP_NOTE -> MessageEvent.USER_NOTE_INPUT_VALIDATE;
            default -> null;
        };
        stateMachine.getExtendedState().getVariables().put(ContextVariable.MESSAGE, message.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<CreateReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        MessageEvent messageEvent = null;
        CreateReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVariable.DATE, userInput);
                messageEvent = MessageEvent.USER_DATE_INPUT_VALIDATE;
            }
            case USER_TIME_INPUTTING -> {
                variables.put(ContextVariable.REPORT_TIME, userInput);
                messageEvent = MessageEvent.USER_TIME_INPUT_VALIDATE;
            }
            case USER_NOTE_INPUTTING -> {
                variables.put(ContextVariable.REPORT_NOTE, userInput);
                messageEvent = MessageEvent.USER_NOTE_INPUT_VALIDATE;
            }
        }

        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);

    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId, String telegramNickname) {
        StateMachine<CreateReportState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("Create_report", telegramNickname));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

