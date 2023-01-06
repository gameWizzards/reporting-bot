package com.telegram.reporting.dialogs.manager_dialogs.add_employee;

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
public class AddEmployeeDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<AddEmployeeState, AddEmployeeEvent>> stateMachines = new ConcurrentHashMap<>();

    private final StateMachineFactory<AddEmployeeState, AddEmployeeEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<AddEmployeeState, AddEmployeeEvent> stateMachine = stateMachines.get(chatId);
        AddEmployeeEvent messageEvent = switch (buttonLabelKey) {
            case MAE_START_DIALOG -> AddEmployeeEvent.RUN_ADD_EMPLOYEE_DIALOG;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Add employee] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<AddEmployeeState, AddEmployeeEvent> stateMachine = stateMachines.get(chatId);
        AddEmployeeState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        AddEmployeeEvent messageEvent = switch (currentState) {
            case USER_PHONE_INPUTTING -> {
                variables.put(ContextVarKey.PHONE, userInput);
                yield AddEmployeeEvent.VALIDATE_PHONE_INPUT;
            }
            default -> null;
        };

        Optional.ofNullable(messageEvent)
                .ifPresent(event -> stateMachine
                        .sendEvent(Mono.just(new GenericMessage<>(event)))
                        .subscribe());
    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<AddEmployeeState, AddEmployeeEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("AddEmployee", chatId));
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
        return ButtonLabelKey.MAE_START_DIALOG;
    }
}