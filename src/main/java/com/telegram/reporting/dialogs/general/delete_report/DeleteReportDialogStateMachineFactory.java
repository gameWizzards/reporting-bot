package com.telegram.reporting.dialogs.general.delete_report;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.DeleteReportActions;
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
@EnableStateMachineFactory(name = "DeleteReportDialogStateMachineFactory")
public class DeleteReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<DeleteReportState, MessageEvent> {
    private final GuardValidator guardValidator;
    private final GeneralActions generalActions;
    private final DeleteReportActions deleteReportActions;

    public DeleteReportDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                 @Lazy GeneralActions generalActions,
                                                 @Lazy DeleteReportActions deleteReportActions) {
        this.guardValidator = guardValidator;
        this.generalActions = generalActions;
        this.deleteReportActions = deleteReportActions;
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
                .action(generalActions::generalRequestInputDate)

                .and().withExternal()
                .source(DeleteReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_DATE_INPUT)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(generalActions::handleUserDateInput)
                .action(generalActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .action(generalActions::generalRequestInputDate)

                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.CHOOSE_TIME_RECORD)
                .target(DeleteReportState.USER_DELETE_CONFIRMATION)
                .action(generalActions::handleChoiceTimeRecord)
                .action(deleteReportActions::requestDeleteConfirmation)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(MessageEvent.CONFIRM_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(deleteReportActions::removeTimeRecord)
                .action(generalActions::sendListTimeRecords)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(MessageEvent.DECLINE_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(generalActions::sendListTimeRecords);
    }
}
