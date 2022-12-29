package com.telegram.reporting.dialogs.general_dialogs.create_report;

import com.telegram.reporting.dialogs.CommonActions;
import com.telegram.reporting.dialogs.guards.GuardValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "CreateReportDialogStateMachineFactory")
public class CreateReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<CreateReportState, CreateReportEvent> {

    private final CommonActions commonActions;
    private final CreateReportActions createReportActions;
    private final GuardValidator guardValidator;

    @Autowired
    public CreateReportDialogStateMachineFactory(@Lazy CreateReportActions createReportActions,
                                                 @Lazy CommonActions commonActions,
                                                 @Lazy GuardValidator guardValidator) {

        this.createReportActions = createReportActions;
        this.commonActions = commonActions;
        this.guardValidator = guardValidator;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<CreateReportState, CreateReportEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<CreateReportState, CreateReportEvent> states) throws Exception {
        states.withStates()
                .initial(CreateReportState.START_CREATE_REPORT_DIALOG)
                .end(CreateReportState.END_CREATE_REPORT_DIALOG)
                .states(EnumSet.allOf(CreateReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CreateReportState, CreateReportEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(CreateReportState.START_CREATE_REPORT_DIALOG)
                .event(CreateReportEvent.RUN_CREATE_REPORT_DIALOG)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(createReportActions::requestInputDate)

                .and().withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .event(CreateReportEvent.VALIDATE_USER_DATE_INPUT)
                .target(CreateReportState.USER_CATEGORY_CHOICE)
                .guard(guardValidator::validateDate)
                .action(commonActions::handleUserDateInput)
                .action(createReportActions::sendExistedTimeRecords)
                .action(createReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(CreateReportEvent.CHOOSE_REPORT_CATEGORY)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .action(commonActions::handleCategory)
                .action(createReportActions::requestInputTime)

                // if all categories are occupied on USER_DATE_INPUTTING step
                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(CreateReportEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(commonActions::requestInputDate)

                // if all categories are occupied on USER_CREATE_ADDITIONAL_REPORT step
                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(CreateReportEvent.GO_TO_USER_FINAL_REPORT_CONFIRMATION)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(createReportActions::requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(CreateReportEvent.VALIDATE_USER_TIME_INPUT)
                .target(CreateReportState.USER_NOTE_INPUTTING)
                .guard(guardValidator::validateTime)
                .action(commonActions::handleUserTimeInput)
                .action(createReportActions::requestInputNote)

                .and().withExternal()
                .source(CreateReportState.USER_NOTE_INPUTTING)
                .event(CreateReportEvent.VALIDATE_USER_NOTE_INPUT)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .guard(guardValidator::validateNote)
                .action(commonActions::handleUserNoteInput)
                .action(createReportActions::prepareTimeRecord)
                .action(createReportActions::requestAdditionalReport)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(CreateReportEvent.CONFIRM_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_CATEGORY_CHOICE)
                .action(createReportActions::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(CreateReportEvent.DECLINE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(createReportActions::requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(CreateReportEvent.CONFIRM_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_CREATE_REPORT_DIALOG)
                .action(createReportActions::persistReport)
                .action(commonActions::startRootMenuFlow)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(CreateReportEvent.DECLINE_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_CREATE_REPORT_DIALOG)
                .action(createReportActions::declinePersistReport)
                .action(commonActions::startRootMenuFlow);

    }
}
