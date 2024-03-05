package com.telegram.reporting.dialogs.general_dialogs.edit_report;

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
public class EditReportDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<EditReportState, EditReportEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<EditReportState, EditReportEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<EditReportState, EditReportEvent> stateMachine = stateMachines.get(chatId);
        EditReportEvent messageEvent = switch (buttonLabelKey) {
            case GER_START_DIALOG -> EditReportEvent.RUN_EDIT_REPORT_DIALOG;
            case COMMON_INPUT_NEW_DATE -> EditReportEvent.RETURN_TO_USER_DATE_INPUTTING;
            case GER_SPEND_TIME -> EditReportEvent.CHOOSE_EDIT_SPEND_TIME;
            case GER_CATEGORY -> EditReportEvent.CHOOSE_EDIT_CATEGORY;
            case GER_NOTE -> EditReportEvent.CHOOSE_EDIT_NOTE;
            case COMMON_CATEGORY_ON_STORAGE,
                    COMMON_CATEGORY_ON_ORDER,
                    COMMON_CATEGORY_ON_OFFICE,
                    COMMON_CATEGORY_ON_COORDINATION -> EditReportEvent.HANDLE_USER_CHANGE_CATEGORY;

            case GER_CONFIRM_EDIT_ADDITIONAL_DATA -> EditReportEvent.CONFIRM_EDIT_ADDITIONAL_DATA;
            case GER_DECLINE_EDIT_ADDITIONAL_DATA -> EditReportEvent.DECLINE_EDIT_ADDITIONAL_DATA;
            case GER_APPLY_DATA_CHANGES -> EditReportEvent.CONFIRM_EDIT_DATA;
            case COMMON_CANCEL -> EditReportEvent.DECLINE_EDIT_DATA;
            case COMMON_YES -> EditReportEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD;
            case COMMON_NO -> EditReportEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Edit report] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EditReportState, EditReportEvent> stateMachine = stateMachines.get(chatId);
        EditReportState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        EditReportEvent messageEvent = switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                yield EditReportEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVarKey.TIME_RECORD_CHOICE, Long.parseLong(userInput));
                yield EditReportEvent.CHOOSE_TIME_RECORD;
            }
            case USER_CHANGE_SPEND_TIME -> {
                variables.put(ContextVarKey.REPORT_TIME, userInput);
                yield EditReportEvent.HANDLE_USER_CHANGE_SPEND_TIME;
            }
            case USER_CHANGE_NOTE -> {
                variables.put(ContextVarKey.REPORT_NOTE, userInput);
                yield EditReportEvent.HANDLE_USER_CHANGE_NOTE;
            }
            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(event ->
                        stateMachine.sendEvent(Mono.just(new GenericMessage<>(event)))
                        .subscribe());

    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<EditReportState, EditReportEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("Edit_report", chatId));
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
        return ButtonLabelKey.GER_START_DIALOG;
    }
}

