package com.telegram.reporting.dialogs.tariff_dialogs.edit_employee_tariff;

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
public class EditEmployeeTariffDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<EditEmployeeTariffState, EditEmployeeTariffEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<EditEmployeeTariffState, EditEmployeeTariffEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<EditEmployeeTariffState, EditEmployeeTariffEvent> stateMachine = stateMachines.get(chatId);
        EditEmployeeTariffEvent messageEvent = switch (buttonLabelKey) {
            case GER_START_DIALOG -> EditEmployeeTariffEvent.RUN_EDIT_EMPLOYEE_TARIFF_DIALOG;
            case COMMON_INPUT_NEW_DATE -> EditEmployeeTariffEvent.RETURN_TO_USER_DATE_INPUTTING;
            case GER_SPEND_TIME -> EditEmployeeTariffEvent.CHOOSE_EDIT_SPEND_TIME;
            case GER_CATEGORY -> EditEmployeeTariffEvent.CHOOSE_EDIT_CATEGORY;
            case GER_NOTE -> EditEmployeeTariffEvent.CHOOSE_EDIT_NOTE;
            case COMMON_CATEGORY_ON_STORAGE,
                    COMMON_CATEGORY_ON_ORDER,
                    COMMON_CATEGORY_ON_OFFICE,
                    COMMON_CATEGORY_ON_COORDINATION -> EditEmployeeTariffEvent.HANDLE_USER_CHANGE_CATEGORY;

            case GER_CONFIRM_EDIT_ADDITIONAL_DATA -> EditEmployeeTariffEvent.CONFIRM_EDIT_ADDITIONAL_DATA;
            case GER_DECLINE_EDIT_ADDITIONAL_DATA -> EditEmployeeTariffEvent.DECLINE_EDIT_ADDITIONAL_DATA;
            case GER_APPLY_DATA_CHANGES -> EditEmployeeTariffEvent.CONFIRM_EDIT_DATA;
            case COMMON_CANCEL -> EditEmployeeTariffEvent.DECLINE_EDIT_DATA;
            case COMMON_YES -> EditEmployeeTariffEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD;
            case COMMON_NO -> EditEmployeeTariffEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Edit employee tariff] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EditEmployeeTariffState, EditEmployeeTariffEvent> stateMachine = stateMachines.get(chatId);
        EditEmployeeTariffState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        EditEmployeeTariffEvent messageEvent = switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                yield EditEmployeeTariffEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVarKey.TIME_RECORD_CHOICE, Long.parseLong(userInput));
                yield EditEmployeeTariffEvent.CHOOSE_TIME_RECORD;
            }
            case USER_CHANGE_SPEND_TIME -> {
                variables.put(ContextVarKey.REPORT_TIME, userInput);
                yield EditEmployeeTariffEvent.HANDLE_USER_CHANGE_SPEND_TIME;
            }
            case USER_CHANGE_NOTE -> {
                variables.put(ContextVarKey.REPORT_NOTE, userInput);
                yield EditEmployeeTariffEvent.HANDLE_USER_CHANGE_NOTE;
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
        StateMachine<EditEmployeeTariffState, EditEmployeeTariffEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("EditEmployeeTariff", chatId));
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
        return ButtonLabelKey.TEE_START_DIALOG;
    }
}

