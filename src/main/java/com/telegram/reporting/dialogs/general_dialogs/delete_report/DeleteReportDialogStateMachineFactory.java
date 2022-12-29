package com.telegram.reporting.dialogs.general_dialogs.delete_report;

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
@EnableStateMachineFactory(name = "DeleteReportDialogStateMachineFactory")
public class DeleteReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<DeleteReportState, DeleteReportEvent> {
    private final GuardValidator guardValidator;
    private final CommonActions commonActions;
    private final DeleteReportActions deleteReportActions;

    public DeleteReportDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                 @Lazy CommonActions commonActions,
                                                 @Lazy DeleteReportActions deleteReportActions) {
        this.guardValidator = guardValidator;
        this.commonActions = commonActions;
        this.deleteReportActions = deleteReportActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<DeleteReportState, DeleteReportEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<DeleteReportState, DeleteReportEvent> states) throws Exception {
        states.withStates()
                .initial(DeleteReportState.START_DELETE_REPORT_DIALOG)
                .end(DeleteReportState.END_DELETE_REPORT_DIALOG)
                .states(EnumSet.allOf(DeleteReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DeleteReportState, DeleteReportEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(DeleteReportState.START_DELETE_REPORT_DIALOG)
                .event(DeleteReportEvent.RUN_DELETE_REPORT_DIALOG)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(DeleteReportState.USER_DATE_INPUTTING)
                .event(DeleteReportEvent.VALIDATE_USER_DATE_INPUT)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(commonActions::handleUserDateInput)
                .action(commonActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .event(DeleteReportEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(DeleteReportState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .event(DeleteReportEvent.CHOOSE_TIME_RECORD)
                .target(DeleteReportState.USER_DELETE_CONFIRMATION)
                .action(commonActions::handleChoiceTimeRecord)
                .action(deleteReportActions::requestDeleteConfirmation)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(DeleteReportEvent.CONFIRM_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(deleteReportActions::removeTimeRecord)
                .action(commonActions::sendListTimeRecords)

                .and().withExternal()
                .source(DeleteReportState.USER_DELETE_CONFIRMATION)
                .event(DeleteReportEvent.DECLINE_DELETE_TIME_RECORD)
                .target(DeleteReportState.USER_TIME_RECORD_CHOICE)
                .action(commonActions::sendListTimeRecords);
    }
}
