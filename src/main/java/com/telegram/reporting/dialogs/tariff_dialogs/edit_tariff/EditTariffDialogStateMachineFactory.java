package com.telegram.reporting.dialogs.tariff_dialogs.edit_tariff;

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
@EnableStateMachineFactory(name = "EditTariffDialogStateMachineFactory")
public class EditTariffDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EditTariffState, EditTariffEvent> {
    private final GuardValidator guardValidator;
    private final CommonActions commonActions;
    private final EditTariffActions editReportActions;

    public EditTariffDialogStateMachineFactory(@Lazy GuardValidator guardValidator,
                                               @Lazy CommonActions commonActions,
                                               @Lazy EditTariffActions editReportActions) {
        this.guardValidator = guardValidator;
        this.commonActions = commonActions;
        this.editReportActions = editReportActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EditTariffState, EditTariffEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EditTariffState, EditTariffEvent> states) throws Exception {
        states.withStates()
                .initial(EditTariffState.START_EDIT_TARIFF_DIALOG)
                .end(EditTariffState.END_EDIT_TARIFF_DIALOG)
                .states(EnumSet.allOf(EditTariffState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EditTariffState, EditTariffEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EditTariffState.START_EDIT_TARIFF_DIALOG)
                .event(EditTariffEvent.RUN_EDIT_TARIFF_DIALOG)
                .target(EditTariffState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditTariffState.USER_DATE_INPUTTING)
                .event(EditTariffEvent.VALIDATE_USER_DATE_INPUT)
                .target(EditTariffState.USER_TIME_RECORD_CHOICE)
                .guard(guardValidator::validateDate)
                .action(commonActions::handleUserDateInput)
                .action(commonActions::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(EditTariffState.USER_TIME_RECORD_CHOICE)
                .event(EditTariffEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EditTariffState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                .and().withExternal()
                .source(EditTariffState.USER_TIME_RECORD_CHOICE)
                .event(EditTariffEvent.CHOOSE_TIME_RECORD)
                .target(EditTariffState.USER_EDIT_DATA_CHOICE)
                .action(commonActions::handleChoiceTimeRecord)
                .action(editReportActions::requestChooseEditData)

                //---------------- Handle Time record data -------------------
                // SPEND_TIME handling
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditTariffEvent.CHOOSE_EDIT_SPEND_TIME)
                .target(EditTariffState.USER_CHANGE_SPEND_TIME)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditTariffState.USER_CHANGE_SPEND_TIME)
                .event(EditTariffEvent.HANDLE_USER_CHANGE_SPEND_TIME)
                .target(EditTariffState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateTime)
                .action(commonActions::handleUserTimeInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // CATEGORY handling
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditTariffEvent.CHOOSE_EDIT_CATEGORY)
                .target(EditTariffState.USER_CHANGE_CATEGORY)
                .action(editReportActions::sendDataToEdit)
                .action(editReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(EditTariffState.USER_CHANGE_CATEGORY)
                .event(EditTariffEvent.HANDLE_USER_CHANGE_CATEGORY)
                .target(EditTariffState.USER_EDIT_ADDITIONAL_DATA)
                .action(commonActions::handleCategory)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                // NOTE handling
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_DATA_CHOICE)
                .event(EditTariffEvent.CHOOSE_EDIT_NOTE)
                .target(EditTariffState.USER_CHANGE_NOTE)
                .action(editReportActions::sendDataToEdit)

                .and().withExternal()
                .source(EditTariffState.USER_CHANGE_NOTE)
                .event(EditTariffEvent.HANDLE_USER_CHANGE_NOTE)
                .target(EditTariffState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardValidator::validateNote)
                .action(commonActions::handleUserNoteInput)
                .action(editReportActions::editTimeRecord)
                .action(editReportActions::requestEditAdditionalData)

                //---------------------------------------------------------

                // return to current TimeRecord data
                // yes
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditTariffEvent.CONFIRM_EDIT_ADDITIONAL_DATA)
                .target(EditTariffState.USER_EDIT_DATA_CHOICE)
                .action(editReportActions::requestChooseEditData)

                // no
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_ADDITIONAL_DATA)
                .event(EditTariffEvent.DECLINE_EDIT_ADDITIONAL_DATA)
                .target(EditTariffState.USER_EDIT_DATA_CONFIRMATION)
                .action(editReportActions::requestSaveTimeRecordChanges)

                // save edited TimeRecord
                // YES
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditTariffEvent.CONFIRM_EDIT_DATA)
                .target(EditTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::saveTimeRecordChanges)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // NO
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_DATA_CONFIRMATION)
                .event(EditTariffEvent.DECLINE_EDIT_DATA)
                .target(EditTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActions::requestEditAdditionalTimeRecord)

                // return to list TimeRecords of current date
                //YES
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditTariffEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditTariffState.USER_TIME_RECORD_CHOICE)
                .action(commonActions::sendListTimeRecords)


                // NO
                .and().withExternal()
                .source(EditTariffState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(EditTariffEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditTariffState.END_EDIT_TARIFF_DIALOG)
                .action(commonActions::startRootMenuFlow);
    }
}
