package com.telegram.reporting.dialogs.general_dialogs.create_report;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DefaultDialogListener;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateReportDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<CreateReportState, CreateReportEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<CreateReportState, CreateReportEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<CreateReportState, CreateReportEvent> stateMachine = stateMachines.get(chatId);
        CreateReportEvent createReportEvent = switch (buttonLabelKey) {
            case GCR_START_DIALOG -> CreateReportEvent.RUN_CREATE_REPORT_DIALOG;
            case COMMON_CATEGORY_ON_STORAGE,
                    COMMON_CATEGORY_ON_ORDER,
                    COMMON_CATEGORY_ON_OFFICE,
                    COMMON_CATEGORY_ON_COORDINATION -> CreateReportEvent.CHOOSE_REPORT_CATEGORY;
            case COMMON_YES -> CreateReportEvent.CONFIRM_ADDITIONAL_REPORT;
            case COMMON_NO -> CreateReportEvent.DECLINE_ADDITIONAL_REPORT;
            case GCR_SEND_REPORT -> CreateReportEvent.CONFIRM_CREATION_FINAL_REPORT;
            case COMMON_CANCEL -> CreateReportEvent.DECLINE_CREATION_FINAL_REPORT;
            case GCR_SKIP_NOTE -> CreateReportEvent.VALIDATE_USER_NOTE_INPUT;
            case COMMON_INPUT_NEW_DATE -> CreateReportEvent.RETURN_TO_USER_DATE_INPUTTING;
            case COMMON_LIST_TIME_RECORDS -> CreateReportEvent.GO_TO_USER_FINAL_REPORT_CONFIRMATION;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Create report] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(createReportEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<CreateReportState, CreateReportEvent> stateMachine = stateMachines.get(chatId);
        CreateReportEvent createReportEvent = null;
        CreateReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                createReportEvent = CreateReportEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_INPUTTING -> {
                variables.put(ContextVarKey.REPORT_TIME, userInput);
                createReportEvent = CreateReportEvent.VALIDATE_USER_TIME_INPUT;
            }
            case USER_NOTE_INPUTTING -> {
                variables.put(ContextVarKey.REPORT_NOTE, userInput);
                createReportEvent = CreateReportEvent.VALIDATE_USER_NOTE_INPUT;
            }
        }

        Optional.ofNullable(createReportEvent)
                .ifPresent(event -> stateMachine
                        .sendEvent(Mono.just(new GenericMessage<>(event)))
                        .subscribe());

    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<CreateReportState, CreateReportEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("Create_report", chatId));
        stateMachine.addStateListener(new DefaultDialogListener<>(stateMachine));
        stateMachines.put(chatId, stateMachine);
        return this;
    }

    @Override
    public void removeDialogData(Long chatId) {
        stateMachines.get(chatId).getExtendedState().getVariables().clear();
    }

    @Override
    public ButtonLabelKey startDialogButtonKey() {
        return ButtonLabelKey.GCR_START_DIALOG;
    }
}

