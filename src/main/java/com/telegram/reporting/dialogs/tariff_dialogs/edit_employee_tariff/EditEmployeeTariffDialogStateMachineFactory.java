package com.telegram.reporting.dialogs.tariff_dialogs.edit_employee_tariff;

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
@EnableStateMachineFactory(name = "EditEmployeeTariffDialogStateMachineFactory")
public class EditEmployeeTariffDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EditEmployeeTariffState, EditEmployeeTariffEvent> {
    private final GuardValidator guardValidator;
    private final CommonActions commonActions;
    private final EditEmployeeTariffActions editReportActions;

    public EditEmployeeTariffDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                                       @Lazy CommonActions commonActions,
                                                       @Lazy EditEmployeeTariffActions editReportActions) {
        this.guardValidator = guardValidator;
        this.commonActions = commonActions;
        this.editReportActions = editReportActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EditEmployeeTariffState, EditEmployeeTariffEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EditEmployeeTariffState, EditEmployeeTariffEvent> states) throws Exception {
        states.withStates()
                .initial(EditEmployeeTariffState.START_EDIT_EMPLOYEE_TARIFF_DIALOG)
                .end(EditEmployeeTariffState.END_EDIT_REPORT_DIALOG)
                .states(EnumSet.allOf(EditEmployeeTariffState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EditEmployeeTariffState, EditEmployeeTariffEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EditEmployeeTariffState.START_EDIT_EMPLOYEE_TARIFF_DIALOG)
                .event(EditEmployeeTariffEvent.RUN_EDIT_EMPLOYEE_TARIFF_DIALOG)
                .target(EditEmployeeTariffState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditEmployeeTariffState.USER_DATE_INPUTTING)
                .event(EditEmployeeTariffEvent.VALIDATE_USER_DATE_INPUT)
                .target(EditEmployeeTariffState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(commonActions::handleUserDateInput)
                .action(commonActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_TIME_RECORD_CHOICE)
                .event(EditEmployeeTariffEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EditEmployeeTariffState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditEmployeeTariffState.USER_TIME_RECORD_CHOICE)
                .event(EditEmployeeTariffEvent.CHOOSE_TIME_RECORD)
                .target(EditEmployeeTariffState.USER_EDIT_DATA_CHOICE)
                .action(commonActions::handleChoiceTimeRecord)
                .action(editReportActions::requestChooseEditData)

                //---------------- Handle Time record data -------------------
                // SPEND_TIME handling
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditEmployeeTariffEvent.CHOOSE_EDIT_SPEND_TIME)
                .target(EditEmployeeTariffState.USER_CHANGE_SPEND_TIME)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditEmployeeTariffState.USER_CHANGE_SPEND_TIME)
                .event(EditEmployeeTariffEvent.HANDLE_USER_CHANGE_SPEND_TIME)
                .target(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateTime)
                .action(commonActions::handleUserTimeInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // CATEGORY handling
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditEmployeeTariffEvent.CHOOSE_EDIT_CATEGORY)
                .target(EditEmployeeTariffState.USER_CHANGE_CATEGORY)
                .action(editReportActions::sendDataToEdit)
                .action(editReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(EditEmployeeTariffState.USER_CHANGE_CATEGORY)
                .event(EditEmployeeTariffEvent.HANDLE_USER_CHANGE_CATEGORY)
                .target(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_DATA)
                .action(commonActions::handleCategory)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // NOTE handling
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditEmployeeTariffEvent.CHOOSE_EDIT_NOTE)
                .target(EditEmployeeTariffState.USER_CHANGE_NOTE)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditEmployeeTariffState.USER_CHANGE_NOTE)
                .event(EditEmployeeTariffEvent.HANDLE_USER_CHANGE_NOTE)
                .target(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateNote)
                .action(commonActions::handleUserNoteInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                //---------------------------------------------------------

                // return to current TimeRecord data
                // yes
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditEmployeeTariffEvent.CONFIRM_EDIT_ADDITIONAL_DATA)
                .target(EditEmployeeTariffState.USER_EDIT_DATA_CHOICE)
                .action(editReportActions::requestChooseEditData)

                // no
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditEmployeeTariffEvent.DECLINE_EDIT_ADDITIONAL_DATA)
                .target(EditEmployeeTariffState.USER_EDIT_DATA_CONFIRMATION)
                .action(editReportActions::requestSaveTimeRecordChanges)

                // save edited TimeRecord
                // YES
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditEmployeeTariffEvent.CONFIRM_EDIT_DATA)
                .target(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::saveTimeRecordChanges)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // NO
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditEmployeeTariffEvent.DECLINE_EDIT_DATA)
                .target(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // return to list TimeRecords of current date
                //YES
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditEmployeeTariffEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditEmployeeTariffState.USER_TIME_RECORD_CHOICE)
                .action(commonActions::sendListTimeRecords)


                // NO
                .and().withExternal()
                .source(EditEmployeeTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditEmployeeTariffEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditEmployeeTariffState.END_EDIT_REPORT_DIALOG)
                .action(commonActions::startRootMenuFlow);
    }
}
