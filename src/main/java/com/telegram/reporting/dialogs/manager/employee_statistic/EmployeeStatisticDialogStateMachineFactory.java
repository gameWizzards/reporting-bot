package com.telegram.reporting.dialogs.manager.employee_statistic;

import com.telegram.reporting.dialogs.GeneralDialogListener;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.EmployeeStatisticActions;
import com.telegram.reporting.dialogs.actions.GeneralActions;
import com.telegram.reporting.dialogs.guards.GuardValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "EmployeeStatisticDialogStateMachineFactory")
public class EmployeeStatisticDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EmployeeStatisticState, MessageEvent> {
    private final GuardValidator guardValidator;
    private final GeneralActions generalActions;
    private final EmployeeStatisticActions employeeStatisticActions;

    public EmployeeStatisticDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                      @Lazy GeneralActions generalActions,
                                                      @Lazy EmployeeStatisticActions employeeStatisticActions) {
        this.guardValidator = guardValidator;
        this.generalActions = generalActions;
        this.employeeStatisticActions = employeeStatisticActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeStatisticState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new GeneralDialogListener())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmployeeStatisticState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(EmployeeStatisticState.START_EMPLOYEE_STATISTIC_DIALOG)
                .end(EmployeeStatisticState.END_EMPLOYEE_STATISTIC_DIALOG)
                .states(EnumSet.allOf(EmployeeStatisticState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeStatisticState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EmployeeStatisticState.START_EMPLOYEE_STATISTIC_DIALOG)
                .event(MessageEvent.RUN_EMPLOYEE_STATISTIC_DIALOG)
                .target(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .action(employeeStatisticActions::requestInputMonth)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_MONTH_INPUT)
                .target(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .guard(guardValidator::validateMonthDate)
                .action(generalActions::handleUserMonthInput)
                .action(employeeStatisticActions::sendListUsers)

                // if reports for chosen month don't exist
                .and().withExternal()
                .source(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .action(generalActions::generalRequestInputDate)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.CHOOSE_EMPLOYEE)
                .target(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .action(employeeStatisticActions::sendListTimeRecords)
                .action(employeeStatisticActions::requestToLockDataToEdit)

                // on choose YES to change lock status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .event(MessageEvent.CONFIRM_CHANGE_LOCK_REPORT_STATUS)
                .target(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .action(employeeStatisticActions::sendLockReportStatusInfo)

                // on choose NO to change lock status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .event(MessageEvent.DECLINE_CHANGE_LOCK_REPORT_STATUS)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                // change lock report status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .event(MessageEvent.HANDLE_LOCK_REPORT_DATA_TO_EDIT)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::handleChoiceLockReportDataToEdit)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                // on cancel to change lock edit report status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .event(MessageEvent.CHOOSE_ANOTHER_EMPLOYEE)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.CHOOSE_ANOTHER_EMPLOYEE)
                .target(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::sendListUsers);
    }
}
