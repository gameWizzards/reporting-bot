package com.telegram.reporting.dialogs.delete_report;

import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.GuardService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "DeleteReportDialogStateMachineFactory")
public class DeleteReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<DeleteReportState, MessageEvent> {
    private final GuardService guardService;

    public DeleteReportDialogStateMachineFactory(@Lazy GuardService guardService) {
        this.guardService = guardService;
    }
    @Override
    public void configure(StateMachineConfigurationConfigurer<DeleteReportState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new DeleteReportDialogListenerImpl())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<DeleteReportState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(DeleteReportState.USER_DATE_INPUTTING)
                .end(DeleteReportState.END_DIALOG)
                .states(EnumSet.allOf(DeleteReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DeleteReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(DeleteReportState.USER_DATE_INPUTTING)
                .target(DeleteReportState.DATE_VALIDATION)
//                .event(MessageEvent.USER_DATE_INPUT)
//                .action(reservedAction(), errorAction())
                .guard(guardService::validateDate)

                .and().withExternal()
                .source(DeleteReportState.DATE_VALIDATION)
                .target(DeleteReportState.LIST_TIME_RECORDS_TO_CHOOSE)
//                .event(MessageEvent.VALID_DATE)
//                .guard(hideGuard())
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(DeleteReportState.LIST_TIME_RECORDS_TO_CHOOSE)
                .target(DeleteReportState.USER_TIME_RECORD_CHOOSE)
//                .event(MessageEvent.INVALID_DATE)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOOSE)
                .target(DeleteReportState.USER_DELETE_CONFIRMATION)
//                .event(MessageEvent.CHOICE_REPORT_CATEGORY)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .target(DeleteReportState.END_DIALOG)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
//                .action(reservedAction(), errorAction())

                // Handling CANCEL button
                .and().withExternal()
                .source(DeleteReportState.LIST_TIME_RECORDS_TO_CHOOSE)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.CANCEL)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOOSE)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.CANCEL)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.CANCEL)
//                .action(reservedAction(), errorAction())


                // Handling RETURN_TO_MAIN_MENU button
                .and().withExternal()
                .source(DeleteReportState.USER_DATE_INPUTTING)
                .target(DeleteReportState.END_DIALOG)
                .event(MessageEvent.RETURN_TO_MAIN_MENU);
//                .action(reservedAction(), errorAction());


    }
}
