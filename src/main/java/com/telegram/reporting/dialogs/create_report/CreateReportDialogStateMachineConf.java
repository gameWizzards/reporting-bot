package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.listener.CreateReportDialogListenerImpl;
import com.telegram.reporting.messages.MessageEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class CreateReportDialogStateMachineConf extends EnumStateMachineConfigurerAdapter<CreateReportState, MessageEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<CreateReportState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new CreateReportDialogListenerImpl())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<CreateReportState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(CreateReportState.USER_DATE_INPUTTING)
                .end(CreateReportState.END_DIALOG)
                .states(EnumSet.allOf(CreateReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CreateReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .target(CreateReportState.DATE_VALIDATION)
                .event(MessageEvent.USER_DATE_INPUT)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.DATE_VALIDATION)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.VALID_DATE)
//                .guard(hideGuard())
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.DATE_VALIDATION)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.INVALID_DATE)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.CHOICE_REPORT_CATEGORY)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .target(CreateReportState.TIME_VALIDATION)
                .event(MessageEvent.USER_TIME_INPUT)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.TIME_VALIDATION)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.VALID_TIME)
//                .guard(hideGuard())
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.TIME_VALIDATION)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.INVALID_TIME)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.CONFIRM_ADDITIONAL_REPORT)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.DECLINE_ADDITIONAL_REPORT)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .target(CreateReportState.END_DIALOG)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.DECLINE_CREATION_FINAL_REPORT)
//                .action(reservedAction(), errorAction())


            // Handling CANCEL button
                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.CANCEL)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.CANCEL)
//                .action(reservedAction(), errorAction())


            // Handling RETURN_TO_MAIN_MENU button
                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .target(CreateReportState.END_DIALOG)
                .event(MessageEvent.RETURN_TO_MAIN_MENU);
//                .action(reservedAction(), errorAction());


    }
}
