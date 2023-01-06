package com.telegram.reporting.dialogs.manager_dialogs.employee_statistic;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeStatisticDialogProcessor implements DialogProcessor {

    private final StateMachineFactory<EmployeeStatisticState, EmployeeStatisticEvent> stateMachineFactory;
    private final Map<Long, StateMachine<EmployeeStatisticState, EmployeeStatisticEvent>> stateMachines;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<EmployeeStatisticState, EmployeeStatisticEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatisticEvent messageEvent = switch (buttonLabelKey) {
            case MES_START_DIALOG -> EmployeeStatisticEvent.RUN_EMPLOYEE_STATISTIC_DIALOG;
            case COMMON_INPUT_NEW_DATE -> EmployeeStatisticEvent.RETURN_TO_USER_DATE_INPUTTING;
            case COMMON_YES -> EmployeeStatisticEvent.CONFIRM_CHANGE_LOCK_REPORT_STATUS;
            case COMMON_NO -> EmployeeStatisticEvent.DECLINE_CHANGE_LOCK_REPORT_STATUS;
            case MES_LOCK_EDIT_REPORT_DATA,
                    MES_UNLOCK_EDIT_REPORT_DATA -> EmployeeStatisticEvent.HANDLE_LOCK_REPORT_DATA_TO_EDIT;
            case MES_CHOICE_ANOTHER_EMPLOYEE,
                    COMMON_CANCEL -> EmployeeStatisticEvent.CHOOSE_ANOTHER_EMPLOYEE;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Employee statistic] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<EmployeeStatisticState, EmployeeStatisticEvent> stateMachine = stateMachines.get(chatId);
        EmployeeStatisticState currentState = stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();

        EmployeeStatisticEvent messageEvent = switch (currentState) {
            case USER_MONTH_INPUTTING -> {
                variables.put(ContextVarKey.DATE, userInput);
                yield EmployeeStatisticEvent.VALIDATE_USER_MONTH_INPUT;
            }
            case USER_EMPLOYEE_CHOOSING -> {
                variables.put(ContextVarKey.EMPLOYEE_ORDINAL, userInput);
                yield EmployeeStatisticEvent.CHOOSE_EMPLOYEE;
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
        StateMachine<EmployeeStatisticState, EmployeeStatisticEvent> stateMachine = stateMachineFactory.getStateMachine();
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
        return ButtonLabelKey.MES_START_DIALOG;
    }
}