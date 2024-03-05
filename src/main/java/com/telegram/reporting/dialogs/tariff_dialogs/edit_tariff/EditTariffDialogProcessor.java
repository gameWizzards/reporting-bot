package com.telegram.reporting.dialogs.tariff_dialogs.edit_tariff;

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
public class EditTariffDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<EditTariffState, EditTariffEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<EditTariffState, EditTariffEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<EditTariffState, EditTariffEvent> stateMachine = stateMachines.get(chatId);
        EditTariffEvent messageEvent = switch (buttonLabelKey) {
            case GER_START_DIALOG -> EditTariffEvent.RUN_EDIT_TARIFF_DIALOG;
            case COMMON_INPUT_NEW_DATE -> EditTariffEvent.RETURN_TO_USER_DATE_INPUTTING;
            case GER_SPEND_TIME -> EditTariffEvent.CHOOSE_EDIT_SPEND_TIME;
            case GER_CATEGORY -> EditTariffEvent.CHOOSE_EDIT_CATEGORY;
            case GER_NOTE -> EditTariffEvent.CHOOSE_EDIT_NOTE;
            case COMMON_CATEGORY_ON_STORAGE,
                    COMMON_CATEGORY_ON_ORDER,
                    COMMON_CATEGORY_ON_OFFICE,
                    COMMON_CATEGORY_ON_COORDINATION -> EditTariffEvent.HANDLE_USER_CHANGE_CATEGORY;

            case GER_CONFIRM_EDIT_ADDITIONAL_DATA -> EditTariffEvent.CONFIRM_EDIT_ADDITIONAL_DATA;
            case GER_DECLINE_EDIT_ADDITIONAL_DATA -> EditTariffEvent.DECLINE_EDIT_ADDITIONAL_DATA;
            case GER_APPLY_DATA_CHANGES -> EditTariffEvent.CONFIRM_EDIT_DATA;
            case COMMON_CANCEL -> EditTariffEvent.DECLINE_EDIT_DATA;
            case COMMON_YES -> EditTariffEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD;
            case COMMON_NO -> EditTariffEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Edit tariff] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EditTariffState, EditTariffEvent> stateMachine = stateMachines.get(chatId);
        EditTariffState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        EditTariffEvent messageEvent = switch (currentState) {
            case USER_DATE_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                yield EditTariffEvent.VALIDATE_USER_DATE_INPUT;
            }
            case USER_TIME_RECORD_CHOICE -> {
                variables.put(ContextVarKey.TIME_RECORD_CHOICE, Long.parseLong(userInput));
                yield EditTariffEvent.CHOOSE_TIME_RECORD;
            }
            case USER_CHANGE_SPEND_TIME -> {
                variables.put(ContextVarKey.REPORT_TIME, userInput);
                yield EditTariffEvent.HANDLE_USER_CHANGE_SPEND_TIME;
            }
            case USER_CHANGE_NOTE -> {
                variables.put(ContextVarKey.REPORT_NOTE, userInput);
                yield EditTariffEvent.HANDLE_USER_CHANGE_NOTE;
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
        StateMachine<EditTariffState, EditTariffEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("EditTariff", chatId));
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
        return ButtonLabelKey.TE_START_DIALOG;
    }
}

