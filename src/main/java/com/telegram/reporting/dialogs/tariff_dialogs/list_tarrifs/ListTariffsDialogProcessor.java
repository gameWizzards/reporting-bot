package com.telegram.reporting.dialogs.tariff_dialogs.list_tarrifs;

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
public class ListTariffsDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<ListTariffsState, ListTariffsEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<ListTariffsState, ListTariffsEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<ListTariffsState, ListTariffsEvent> stateMachine = stateMachines.get(chatId);
        ListTariffsEvent messageEvent = switch (buttonLabelKey) {
            case TL_START_DIALOG -> ListTariffsEvent.LIST_TARIFFS_DIALOG_STARTED;
            case TL_SHOW_OVERRIDDEN_TARIFFS_BY_EMPLOYEE,
                    TL_SHOW_OVERRIDDEN_TARIFFS_BY_CATEGORY -> ListTariffsEvent.DISPLAYING_OVERRIDDEN_TARIFFS_CHOSEN;

            case COMMON_CATEGORY_ON_STORAGE,
                    COMMON_CATEGORY_ON_ORDER,
                    COMMON_CATEGORY_ON_OFFICE,
                    COMMON_CATEGORY_ON_COORDINATION -> ListTariffsEvent.DISPLAYING_BY_CATEGORY_CHOSEN;
            case TL_CHOOSE_ANOTHER_TARIFF -> ListTariffsEvent.RESEND_CATEGORY_BUTTONS;

//            case TL_TARIFFS_BY_CATEGORY -> ListTariffsEvent.DISPLAYING_BY_CATEGORY_CHOSEN;
//            case TL_TARIFFS_BY_EMPLOYEE -> ListTariffsEvent.DISPLAYING_BY_EMPLOYEE_CHOSEN;



            default ->
                    throw new ButtonToEventMappingException(chatId, "[List tariffs] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<ListTariffsState, ListTariffsEvent> stateMachine = stateMachines.get(chatId);
        ListTariffsState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        ListTariffsEvent messageEvent = switch (currentState) {
            case TARIFF_OPTION_CHOICE -> {
                variables.put(ContextVarKey.EMPLOYEE_ORDINAL, Long.parseLong(userInput));
                yield ListTariffsEvent.DISPLAYING_BY_EMPLOYEE_CHOSEN;
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
        StateMachine<ListTariffsState, ListTariffsEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("ListTariffs", chatId));
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
        return ButtonLabelKey.TL_START_DIALOG;
    }
}

