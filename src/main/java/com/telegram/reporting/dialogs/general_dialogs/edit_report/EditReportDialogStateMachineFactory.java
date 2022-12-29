package com.telegram.reporting.dialogs.general_dialogs.edit_report;

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
@EnableStateMachineFactory(name = "EditReportDialogStateMachineFactory")
public class EditReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EditReportState, EditReportEvent> {
    private final GuardValidator guardValidator;
    private final CommonActions commonActions;
    private final EditReportActions editReportActions;

    public EditReportDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                               @Lazy CommonActions commonActions,
                                               @Lazy EditReportActions editReportActions) {
        this.guardValidator = guardValidator;
        this.commonActions = commonActions;
        this.editReportActions = editReportActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EditReportState, EditReportEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EditReportState, EditReportEvent> states) throws Exception {
        states.withStates()
                .initial(EditReportState.START_EDIT_REPORT_DIALOG)
                .end(EditReportState.END_EDIT_REPORT_DIALOG)
                .states(EnumSet.allOf(EditReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EditReportState, EditReportEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EditReportState.START_EDIT_REPORT_DIALOG)
                .event(EditReportEvent.RUN_EDIT_REPORT_DIALOG)
                .target(EditReportState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_DATE_INPUTTING)
                .event(EditReportEvent.VALIDATE_USER_DATE_INPUT)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(commonActions::handleUserDateInput)
                .action(commonActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(EditReportEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EditReportState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(EditReportEvent.CHOOSE_TIME_RECORD)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(commonActions::handleChoiceTimeRecord)
                .action(editReportActions::requestChooseEditData)

                //---------------- Handle Time record data -------------------
                // SPEND_TIME handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(EditReportEvent.CHOOSE_EDIT_SPEND_TIME)
                .target(EditReportState.USER_CHANGE_SPEND_TIME)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_SPEND_TIME)
                .event(EditReportEvent.HANDLE_USER_CHANGE_SPEND_TIME)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateTime)
                .action(commonActions::handleUserTimeInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // CATEGORY handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(EditReportEvent.CHOOSE_EDIT_CATEGORY)
                .target(EditReportState.USER_CHANGE_CATEGORY)
                .action(editReportActions::sendDataToEdit)
                .action(editReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_CATEGORY)
                .event(EditReportEvent.HANDLE_USER_CHANGE_CATEGORY)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .action(commonActions::handleCategory)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // NOTE handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(EditReportEvent.CHOOSE_EDIT_NOTE)
                .target(EditReportState.USER_CHANGE_NOTE)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_NOTE)
                .event(EditReportEvent.HANDLE_USER_CHANGE_NOTE)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateNote)
                .action(commonActions::handleUserNoteInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                //---------------------------------------------------------

                // return to current TimeRecord data
                // yes
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditReportEvent.CONFIRM_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(editReportActions::requestChooseEditData)

                // no
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditReportEvent.DECLINE_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .action(editReportActions::requestSaveTimeRecordChanges)

                // save edited TimeRecord
                // YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditReportEvent.CONFIRM_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::saveTimeRecordChanges)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditReportEvent.DECLINE_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // return to list TimeRecords of current date
                //YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditReportEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .action(commonActions::sendListTimeRecords)


                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditReportEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.END_EDIT_REPORT_DIALOG)
                .action(commonActions::startRootMenuFlow);
    }
}
