package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.service.CreateReportActionService;
import com.telegram.reporting.service.GeneralActionService;
import com.telegram.reporting.service.GuardService;
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
public class CreateReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<CreateReportState, MessageEvent> {

    private final GeneralActionService generalActionService;
    private final CreateReportActionService createReportActionService;
    private final GuardService guardService;

    @Autowired
    public CreateReportDialogStateMachineFactory(@Lazy CreateReportActionService createReportActionService,
                                                 @Lazy GeneralActionService generalActionService,
                                                 @Lazy GuardService guardService) {

        this.createReportActionService = createReportActionService;
        this.generalActionService = generalActionService;
        this.guardService = guardService;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<CreateReportState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new CreateReportDialogListenerImpl())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<CreateReportState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(CreateReportState.START_CREATE_REPORT_DIALOG)
                .end(CreateReportState.END_CREATE_REPORT_DIALOG)
                .states(EnumSet.allOf(CreateReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CreateReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(CreateReportState.START_CREATE_REPORT_DIALOG)
                .event(MessageEvent.RUN_CREATE_REPORT_DIALOG)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(createReportActionService::requestInputDate)

                .and().withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_DATE_INPUT)
                .target(CreateReportState.USER_CATEGORY_CHOICE)
                .guard(guardService::validateDate)
                .action(generalActionService::handleUserDateInput)
                .action(createReportActionService::sendExistedTimeRecords)
                .action(createReportActionService::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(MessageEvent.CHOOSE_REPORT_CATEGORY)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .action(generalActionService::handleCategory)
                .action(createReportActionService::requestInputTime)

                // if all categories are occupied on USER_DATE_INPUTTING step
                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(MessageEvent.RETURN_TO_USER_DATE_INPUTTING)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(generalActionService::generalRequestInputDate)

                // if all categories are occupied on USER_CREATE_ADDITIONAL_REPORT step
                .and().withExternal()
                .source(CreateReportState.USER_CATEGORY_CHOICE)
                .event(MessageEvent.GO_TO_USER_FINAL_REPORT_CONFIRMATION)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(createReportActionService::requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_TIME_INPUT)
                .target(CreateReportState.USER_NOTE_INPUTTING)
                .guard(guardService::validateTime)
                .action(generalActionService::handleUserTimeInput)
                .action(createReportActionService::requestInputNote)

                .and().withExternal()
                .source(CreateReportState.USER_NOTE_INPUTTING)
                .event(MessageEvent.VALIDATE_USER_NOTE_INPUT)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .guard(guardService::validateNote)
                .action(generalActionService::handleUserNoteInput)
                .action(createReportActionService::prepareTimeRecord)
                .action(createReportActionService::requestAdditionalReport)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.CONFIRM_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_CATEGORY_CHOICE)
                .action(createReportActionService::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.DECLINE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(createReportActionService::requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_CREATE_REPORT_DIALOG)
                .action(createReportActionService::persistReport)
                .action(generalActionService::sendRootMenuButtons)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.DECLINE_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_CREATE_REPORT_DIALOG)
                .action(createReportActionService::declinePersistReport)
                .action(generalActionService::sendRootMenuButtons);

    }
}
