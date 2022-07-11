package com.telegram.reporting.dialogs.general.edit_report;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.service.EditReportActionService;
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
@EnableStateMachineFactory(name = "EditReportDialogStateMachineFactory")
public class EditReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<EditReportState, MessageEvent> {
    private final GuardService guardService;
    private final GeneralActionService generalActionService;
    private final EditReportActionService editReportActionService;

    public EditReportDialogStateMachineFactory(@Lazy GuardService guardService,
                                               @Lazy GeneralActionService generalActionService,
                                               @Lazy EditReportActionService editReportActionService) {
        this.guardService = guardService;
        this.generalActionService = generalActionService;
        this.editReportActionService = editReportActionService;
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
                .action(generalActionService::generalRequestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_DATE_INPUT)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .guard(guardService::validateDate)
                .action(generalActionService::handleUserDateInput)
                .action(generalActionService::sendListTimeRecords)

                // if report doesn't exist
                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(EditReportState.USER_DATE_INPUTTING)
                .action(generalActionService::generalRequestInputDate)

                .and().withExternal()
                .source(EditReportState.USER_TIME_RECORD_CHOICE)
                .event(MessageEvent.CHOOSE_TIME_RECORD)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(generalActionService::handleChoiceTimeRecord)
                .action(editReportActionService::requestChooseEditData)

                //---------------- Handle Time record data -------------------
                // SPEND_TIME handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_SPEND_TIME)
                .target(EditReportState.USER_CHANGE_SPEND_TIME)
                .action(editReportActionService::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_SPEND_TIME)
                .event(MessageEvent.HANDLE_USER_CHANGE_SPEND_TIME)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardService::validateTime)
                .action(generalActionService::handleUserTimeInput)
                .action(editReportActionService::editTimeRecord)
                .action(editReportActionService::requestEditAdditionalData)

                // CATEGORY handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_CATEGORY)
                .target(EditReportState.USER_CHANGE_CATEGORY)
                .action(editReportActionService::sendCategoryButtons)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_CATEGORY)
                .event(MessageEvent.HANDLE_USER_CHANGE_CATEGORY)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .action(generalActionService::handleCategory)
                .action(editReportActionService::editTimeRecord)
                .action(editReportActionService::requestEditAdditionalData)

                // NOTE handling
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CHOICE)
                .event(MessageEvent.CHOOSE_EDIT_NOTE)
                .target(EditReportState.USER_CHANGE_NOTE)
                .action(editReportActionService::sendDataToEdit)

                .and().withExternal()
                .source(EditReportState.USER_CHANGE_NOTE)
                .event(MessageEvent.HANDLE_USER_CHANGE_NOTE)
                .target(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .guard(guardService::validateNote)
                .action(generalActionService::handleUserNoteInput)
                .action(editReportActionService::editTimeRecord)
                .action(editReportActionService::requestEditAdditionalData)

                //---------------------------------------------------------

                // return to current TimeRecord data
                // yes
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(MessageEvent.CONFIRM_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CHOICE)
                .action(editReportActionService::requestChooseEditData)

                // no
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_DATA)
                .event(MessageEvent.DECLINE_EDIT_ADDITIONAL_DATA)
                .target(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .action(editReportActionService::requestSaveTimeRecordChanges)

                // YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(MessageEvent.CONFIRM_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActionService::saveTimeRecordChanges)
                .action(editReportActionService::requestEditAdditionalTimeRecord)

                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_DATA_CONFIRMATION)
                .event(MessageEvent.DECLINE_EDIT_DATA)
                .target(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .action(editReportActionService::requestEditAdditionalTimeRecord)

                // return to list TimeRecords of current date
                //YES
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(MessageEvent.CONFIRM_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.USER_TIME_RECORD_CHOICE)
                .action(generalActionService::sendListTimeRecords)


                // NO
                .and().withExternal()
                .source(EditReportState.USER_EDIT_ADDITIONAL_TIME_RECORD)
                .event(MessageEvent.DECLINE_EDIT_ADDITIONAL_TIME_RECORD)
                .target(EditReportState.END_EDIT_REPORT_DIALOG)
                .action(generalActionService::sendRootMenuButtons);
    }
}
