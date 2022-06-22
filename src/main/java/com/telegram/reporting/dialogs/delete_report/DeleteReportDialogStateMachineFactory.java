package com.telegram.reporting.dialogs.delete_report;

import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.DeleteReportActionService;
import com.telegram.reporting.service.GeneralActionService;
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
    private final GeneralActionService generalActionService;
    private final DeleteReportActionService deleteReportActionService;

    public DeleteReportDialogStateMachineFactory(@Lazy GuardService guardService,
                                                 @Lazy GeneralActionService generalActionService,
                                                 @Lazy DeleteReportActionService deleteReportActionService) {
        this.guardService = guardService;
        this.generalActionService = generalActionService;
        this.deleteReportActionService = deleteReportActionService;
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
                .initial(DeleteReportState.START_DELETE_REPORT_DIALOG)
                .end(DeleteReportState.END_DELETE_REPORT_DIALOG)
                .states(EnumSet.allOf(DeleteReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DeleteReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(DeleteReportState.START_DELETE_REPORT_DIALOG)
                .event(MessageEvent.RUN_DELETE_REPORT_DIALOG)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .action(deleteReportActionService::requestInputDate)

                .and().withExternal()
                .source(DeleteReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_DATE_INPUT)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardService::validateDate)
                .action(generalActionService::handleUserDateInput)
                .action(generalActionService::sendListTimeRecords)

                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.CHOOSE_TIME_RECORD)
                .target(DeleteReportState.USER_DELETE_CONFIRMATION)
                .action(generalActionService::handleTimeRecord)
                .action(deleteReportActionService::requestDeleteConfirmation)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(MessageEvent.CONFIRM_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(deleteReportActionService::removeTimeRecord)
                .action(generalActionService::sendListTimeRecords)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(MessageEvent.DECLINE_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(generalActionService::sendListTimeRecords);
    }
}
