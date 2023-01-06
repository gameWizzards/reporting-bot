package com.telegram.reporting.dialogs.manager_dialogs.employee_status;

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
public class EmployeeStatusDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<EmployeeStatusState, EmployeeStatusEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<EmployeeStatusState, EmployeeStatusEvent> stateMachineFactory;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<EmployeeStatusState, EmployeeStatusEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatusEvent messageEvent = switch (buttonLabelKey) {
            case MESTATUS_START_DIALOG -> EmployeeStatusEvent.RUN_EMPLOYEE_STATUS_DIALOG;
            case ALU_USER_STATUS_ACTIVE,
                    ALU_USER_STATUS_DELETED -> EmployeeStatusEvent.HANDLE_USER_CHOICE_LIST;
            case MESTATUS_CHOICE_ANOTHER_LIST_EMPLOYEES -> EmployeeStatusEvent.RETURN_TO_LIST_EMPLOYEES_CHOICE;
            case MESTATUS_CHANGE_EMPLOYEE_STATUS -> EmployeeStatusEvent.HANDLE_EDIT_STATUS_CHOICE;
            case MESTATUS_CHANGE_EMPLOYEE_ROLE -> EmployeeStatusEvent.HANDLE_EDIT_ROLE_CHOICE;
            case MESTATUS_ACTIVATE_EMPLOYEE,
                    MESTATUS_DELETE_EMPLOYEE -> EmployeeStatusEvent.HANDLE_EMPLOYEE_STATUS_CHANGE;
            case MESTATUS_ADD_MANAGER_ROLE,
                    MESTATUS_REMOVE_MANAGER_ROLE -> EmployeeStatusEvent.HANDLE_EMPLOYEE_ROLE_CHANGE;
            case COMMON_CANCEL -> EmployeeStatusEvent.DECLINE_EMPLOYEE_DATA_CHANGE;

            case COMMON_YES -> EmployeeStatusEvent.CONFIRM_EMPLOYEE_ADDITIONAL_DATA_CHANGE;
            case COMMON_NO -> EmployeeStatusEvent.DECLINE_EMPLOYEE_ADDITIONAL_DATA_CHANGE;
            case MES_CHOICE_ANOTHER_EMPLOYEE -> EmployeeStatusEvent.CHOOSE_ANOTHER_EMPLOYEE;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Employee status] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EmployeeStatusState, EmployeeStatusEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatusState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        EmployeeStatusEvent messageEvent = switch (currentState) {
            case USER_EMPLOYEE_CHOOSING -> {
                variables.put(ContextVarKey.EMPLOYEE_ORDINAL, userInput);
                yield EmployeeStatusEvent.CHOOSE_EMPLOYEE;
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
        StateMachine<EmployeeStatusState, EmployeeStatusEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("EmployeeStatistic", chatId));
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
        return ButtonLabelKey.MESTATUS_START_DIALOG;
    }
}
