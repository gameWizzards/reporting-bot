package com.telegram.reporting.dialogs.manager_dialogs.employee_statistic;

import com.telegram.reporting.dialogs.CommonActions;
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
public class EmployeeStatisticDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EmployeeStatisticState, EmployeeStatisticEvent> {
    private final GuardValidator guardValidator;
    private final CommonActions commonActions;
    private final EmployeeStatisticActions employeeStatisticActions;

    public EmployeeStatisticDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                      @Lazy CommonActions commonActions,
                                                      @Lazy EmployeeStatisticActions employeeStatisticActions) {
        this.guardValidator = guardValidator;
        this.commonActions = commonActions;
        this.employeeStatisticActions = employeeStatisticActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeStatisticState, EmployeeStatisticEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmployeeStatisticState, EmployeeStatisticEvent> states) throws Exception {
        states.withStates()
                .initial(EmployeeStatisticState.START_EMPLOYEE_STATISTIC_DIALOG)
                .end(EmployeeStatisticState.END_EMPLOYEE_STATISTIC_DIALOG)
                .states(EnumSet.allOf(EmployeeStatisticState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeStatisticState, EmployeeStatisticEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EmployeeStatisticState.START_EMPLOYEE_STATISTIC_DIALOG)
                .event(EmployeeStatisticEvent.RUN_EMPLOYEE_STATISTIC_DIALOG)
                .target(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .action(employeeStatisticActions::requestInputMonth)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .event(EmployeeStatisticEvent.VALIDATE_USER_MONTH_INPUT)
                .target(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .guard(guardValidator::validateMonthDate)
                .action(commonActions::handleUserMonthInput)
                .action(employeeStatisticActions::sendListUsers)

                // if reports for chosen month don't exist
                .and().withExternal()
                .source(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .event(EmployeeStatisticEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EmployeeStatisticState.USER_MONTH_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .event(EmployeeStatisticEvent.CHOOSE_EMPLOYEE)
                .target(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .action(employeeStatisticActions::sendListTimeRecords)
                .action(employeeStatisticActions::requestToLockDataToEdit)

                // on choose YES to change lock status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .event(EmployeeStatisticEvent.CONFIRM_CHANGE_LOCK_REPORT_STATUS)
                .target(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .action(employeeStatisticActions::sendLockReportStatusInfo)

                // on choose NO to change lock status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHOOSING)
                .event(EmployeeStatisticEvent.DECLINE_CHANGE_LOCK_REPORT_STATUS)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                // change lock report status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .event(EmployeeStatisticEvent.HANDLE_LOCK_REPORT_DATA_TO_EDIT)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::handleChoiceLockReportDataToEdit)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                // on cancel to change lock edit report status
                .and().withExternal()
                .source(EmployeeStatisticState.USER_LOCK_REPORT_STATUS_CHANGING)
                .event(EmployeeStatisticEvent.CHOOSE_ANOTHER_EMPLOYEE)
                .target(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::requestReturnToListEmployees)

                .and().withExternal()
                .source(EmployeeStatisticState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .event(EmployeeStatisticEvent.CHOOSE_ANOTHER_EMPLOYEE)
                .target(EmployeeStatisticState.USER_EMPLOYEE_CHOOSING)
                .action(employeeStatisticActions::sendListUsers);
    }
}
