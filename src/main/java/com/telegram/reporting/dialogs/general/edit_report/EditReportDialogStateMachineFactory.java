package com.telegram.reporting.dialogs.general.edit_report;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.EditReportActions;
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
@EnableStateMachineFactory(name = "EditReportDialogStateMachineFactory")
public class EditReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EditReportState, MessageEvent> {
    private final GuardValidator guardValidator;
    private final GeneralActions generalActions;
    private final EditReportActions editReportActions;

    public EditReportDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                               @Lazy GeneralActions generalActions,
                                               @Lazy EditReportActions editReportActions) {
        this.guardValidator = guardValidator;
        this.generalActions = generalActions;
        this.editReportActions = editReportActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EditReportState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new EditReportDialogListenerImpl())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EditReportState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(EditReportState.START_EDIT_REPORT_DIALOG)
                .end(EditReportState.END_EDIT_REPORT_DIALOG)
                .states(EnumSet.allOf(EditReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EditReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EditReportState.START_EDIT_REPORT_DIALOG)
                .event(MessageEvent.RUN_EDIT_REPORT_DIALOG)
                .target(EditReportState.USER_DATE_INPUTTING)
                .action(generalActions::generalRequestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_DATE_INPUT)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(generalActions::handleUserDateInput)
                .action(generalActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EditReportState.USER_DATE_INPUTTING)
                .action(generalActions::generalRequestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.CHOOSE_TIME_RECORD)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(generalActions::handleChoiceTimeRecord)
                .action(editReportActions::requestChooseEditData)

                //---------------- Handle Time record data -------------------
                // SPEND_TIME handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_SPEND_TIME)
                .target(EditReportState.USER_CHANGE_SPEND_TIME)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_SPEND_TIME)
                .event(MessageEvent.HANDLE_USER_CHANGE_SPEND_TIME)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateTime)
                .action(generalActions::handleUserTimeInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // CATEGORY handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_CATEGORY)
                .target(EditReportState.USER_CHANGE_CATEGORY)
                .action(editReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_CATEGORY)
                .event(MessageEvent.HANDLE_USER_CHANGE_CATEGORY)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .action(generalActions::handleCategory)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // NOTE handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_NOTE)
                .target(EditReportState.USER_CHANGE_NOTE)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_NOTE)
                .event(MessageEvent.HANDLE_USER_CHANGE_NOTE)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateNote)
                .action(generalActions::handleUserNoteInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                //---------------------------------------------------------

                // return to current TimeRecord data
                // yes
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(MessageEvent.CONFIRM_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(editReportActions::requestChooseEditData)

                // no
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(MessageEvent.DECLINE_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .action(editReportActions::requestSaveTimeRecordChanges)

                // YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(MessageEvent.CONFIRM_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::saveTimeRecordChanges)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(MessageEvent.DECLINE_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // return to list TimeRecords of current date
                //YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(MessageEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .action(generalActions::sendListTimeRecords)


                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(MessageEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.END_EDIT_REPORT_DIALOG)
                .action(generalActions::sendRootMenuButtons);
    }
}
