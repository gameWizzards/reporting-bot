package com.telegram.reporting.dialogs.manager.employee_status;

import com.telegram.reporting.dialogs.GeneralDialogListener;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.EmployeeStatusActions;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "EmployeeStatusDialogStateMachineFactory")
public class EmployeeStatusDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EmployeeStatusState, MessageEvent> {
    private final EmployeeStatusActions employeeStatusActions;

    public EmployeeStatusDialogStateMachineFactory(@Lazy EmployeeStatusActions employeeStatusActions) {
        this.employeeStatusActions = employeeStatusActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeStatusState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new GeneralDialogListener())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmployeeStatusState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(EmployeeStatusState.START_EMPLOYEE_STATUS_DIALOG)
                .end(EmployeeStatusState.END_EMPLOYEE_STATUS_DIALOG)
                .states(EnumSet.allOf(EmployeeStatusState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeStatusState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EmployeeStatusState.START_EMPLOYEE_STATUS_DIALOG)
                .event(MessageEvent.RUN_EMPLOYEE_STATUS_DIALOG)
                .target(EmployeeStatusState.USER_LIST_EMPLOYEES_CHOOSING)
                .action(employeeStatusActions::requestListEmployeesChoose)

                .and().withExternal()
                .source(EmployeeStatusState.USER_LIST_EMPLOYEES_CHOOSING)
                .event(MessageEvent.HANDLE_USER_CHOICE_LIST)
                .target(EmployeeStatusState.USER_EMPLOYEE_CHOOSING)
                .action(employeeStatusActions::sendListUsers)

                // if list employees empty
                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.RETURN_TO_LIST_EMPLOYEES_CHOICE)
                .target(EmployeeStatusState.USER_LIST_EMPLOYEES_CHOOSING)
                .action(employeeStatusActions::requestListEmployeesChoose)

                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.CHOOSE_EMPLOYEE)
                .target(EmployeeStatusState.USER_EDIT_OPTIONS_CHOOSING)
                .action(employeeStatusActions::sendEmployeeInfo)

                // edit status
                .and().withExternal()
                .source(EmployeeStatusState.USER_EDIT_OPTIONS_CHOOSING)
                .event(MessageEvent.HANDLE_EDIT_STATUS_CHOICE)
                .target(EmployeeStatusState.USER_EMPLOYEE_STATUS_CHANGING)
                .action(employeeStatusActions::sendEditStatusInfo)

                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_STATUS_CHANGING)
                .event(MessageEvent.HANDLE_EMPLOYEE_STATUS_CHANGE)
                .target(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .action(employeeStatusActions::handleEmployeeEditStatus)
                .action(employeeStatusActions::requestChangeAdditionalData)

                // on choose Cancel to change employee status
                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_STATUS_CHANGING)
                .event(MessageEvent.DECLINE_EMPLOYEE_DATA_CHANGE)
                .target(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .action(employeeStatusActions::requestChangeAdditionalData)

                // edit role
                .and().withExternal()
                .source(EmployeeStatusState.USER_EDIT_OPTIONS_CHOOSING)
                .event(MessageEvent.HANDLE_EDIT_ROLE_CHOICE)
                .target(EmployeeStatusState.USER_EMPLOYEE_ROLE_CHANGING)
                .action(employeeStatusActions::sendEditRoleInfo)

                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_ROLE_CHANGING)
                .event(MessageEvent.HANDLE_EMPLOYEE_ROLE_CHANGE)
                .target(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .action(employeeStatusActions::handleEmployeeEditRole)
                .action(employeeStatusActions::requestChangeAdditionalData)

                // on choose Cancel to change employee role
                .and().withExternal()
                .source(EmployeeStatusState.USER_EMPLOYEE_ROLE_CHANGING)
                .event(MessageEvent.DECLINE_EMPLOYEE_DATA_CHANGE)
                .target(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .action(employeeStatusActions::requestChangeAdditionalData)

                // on confirm to change additional employee data
                .and().withExternal()
                .source(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .event(MessageEvent.CONFIRM_EMPLOYEE_ADDITIONAL_DATA_CHANGE)
                .target(EmployeeStatusState.USER_EDIT_OPTIONS_CHOOSING)
                .action(employeeStatusActions::sendEmployeeInfo)

                // on decline to change additional employee data
                .and().withExternal()
                .source(EmployeeStatusState.USER_ADDITIONAL_DATA_CHANGING)
                .event(MessageEvent.DECLINE_EMPLOYEE_ADDITIONAL_DATA_CHANGE)
                .target(EmployeeStatusState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .action(employeeStatusActions::requestReturnToListEmployees)

                .and().withExternal()
                .source(EmployeeStatusState.USER_ANOTHER_EMPLOYEE_CHOOSING)
                .event(MessageEvent.CHOOSE_ANOTHER_EMPLOYEE)
                .target(EmployeeStatusState.USER_LIST_EMPLOYEES_CHOOSING)
                .action(employeeStatusActions::requestListEmployeesChoose);
    }
}
