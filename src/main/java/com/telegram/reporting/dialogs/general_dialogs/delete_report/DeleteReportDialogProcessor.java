package com.telegram.reporting.dialogs.general_dialogs.delete_report;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DefaultDialogListener;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.i18n.ButtonLabelKey;
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
public class DeleteReportDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<DeleteReportState, DeleteReportEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<DeleteReportState, DeleteReportEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<DeleteReportState, DeleteReportEvent> stateMachine = stateMachines.get(chatId);
        DeleteReportEvent messageEvent = switch (buttonLabelKey) {
            case GDR_START_DIALOG -> DeleteReportEvent.RUN_DELETE_REPORT_DIALOG;
            case COMMON_INPUT_NEW_DATE -> DeleteReportEvent.RETURN_TO_USER_DATE_INPUTTING;
            case GDR_CONFIRM_DELETE_TIME_RECORD -> DeleteReportEvent.CONFIRM_DELETE_TIME_RECORD;
            case COMMON_CANCEL -> DeleteReportEvent.DECLINE_DELETE_TIME_RECORD;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Delete report] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<DeleteReportState, DeleteReportEvent> stateMachine = stateMachines.get(chatId);
        DeleteReportEvent messageEvent = null;
        DeleteReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                messageEvent = DeleteReportEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVarKey.TIME_RECORD_CHOICE, Long.parseLong(userInput));
                messageEvent = DeleteReportEvent.CHOOSE_TIME_RECORD;
            }
        }

        Optional.ofNullable(messageEvent)
                .ifPresent(event -> stateMachine
                        .sendEvent(Mono.just(new GenericMessage<>(event)))
                        .subscribe());
    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<DeleteReportState, DeleteReportEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("Delete_report", chatId));
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
        return ButtonLabelKey.GDR_START_DIALOG;
    }
}

