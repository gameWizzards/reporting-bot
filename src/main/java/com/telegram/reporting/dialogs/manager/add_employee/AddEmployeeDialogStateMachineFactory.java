package com.telegram.reporting.dialogs.manager.add_employee;

import com.telegram.reporting.dialogs.GeneralDialogListener;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.AddEmployeeActions;
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
@EnableStateMachineFactory(name = "AddEmployeeDialogStateMachineFactory")
public class AddEmployeeDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<AddEmployeeState, MessageEvent> {
    private final GuardValidator guardValidator;
    private final AddEmployeeActions addEmployeeActions;

    public AddEmployeeDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                @Lazy AddEmployeeActions addEmployeeActions) {
        this.guardValidator = guardValidator;
        this.addEmployeeActions = addEmployeeActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<AddEmployeeState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new GeneralDialogListener())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<AddEmployeeState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(AddEmployeeState.START_ADD_EMPLOYEE_DIALOG)
                .end(AddEmployeeState.END_ADD_EMPLOYEE_DIALOG)
                .states(EnumSet.allOf(AddEmployeeState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AddEmployeeState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(AddEmployeeState.START_ADD_EMPLOYEE_DIALOG)
                .event(MessageEvent.RUN_ADD_EMPLOYEE_DIALOG)
                .target(AddEmployeeState.USER_PHONE_INPUTTING)
                .action(addEmployeeActions::requestInputEmployeePhone)

                .and().withExternal()
                .source(AddEmployeeState.USER_PHONE_INPUTTING)
                .event(MessageEvent.VALIDATE_PHONE_INPUT)
                .target(AddEmployeeState.END_ADD_EMPLOYEE_DIALOG)
                .guard(guardValidator::validatePhoneInput)
                .action(addEmployeeActions::saveNewEmployeePhone)
                ;
    }
}
