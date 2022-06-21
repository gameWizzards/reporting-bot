package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.LoggingExceptionAction;
import com.telegram.reporting.messages.MessageEvent;
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
    private final LoggingExceptionAction loggingExceptionAction;

    @Autowired
    public CreateReportDialogStateMachineFactory(@Lazy CreateReportActionService createReportActionService,
                                                 @Lazy GeneralActionService generalActionService,
                                                 @Lazy GuardService guardService,
                                                 @Lazy LoggingExceptionAction loggingExceptionAction) {

        this.createReportActionService = createReportActionService;
        this.generalActionService = generalActionService;
        this.guardService = guardService;
        this.loggingExceptionAction = loggingExceptionAction;
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
                .initial(CreateReportState.START_DIALOG)
                .end(CreateReportState.END_DIALOG)
                .states(EnumSet.allOf(CreateReportState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CreateReportState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(CreateReportState.START_DIALOG)
                .event(MessageEvent.CREATE_REPORT_EVENT)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(createReportActionService::requestInputDate) //errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.USER_DATE_INPUT_VALIDATE)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .guard(guardService::validateDate)
                .action(generalActionService::handleUserDateInput)
                .action(createReportActionService::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.CHOICE_REPORT_CATEGORY)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .action(createReportActionService::handleCategory)
                .action(createReportActionService::requestInputTime)

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.USER_TIME_INPUT_VALIDATE)
                .target(CreateReportState.USER_NOTE_INPUTTING)
                .guard(guardService::validateTime)
                .action(generalActionService::handleUserTimeInput)
                .action(generalActionService::requestInputNote)

                .and().withExternal()
                .source(CreateReportState.USER_NOTE_INPUTTING)
                .event(MessageEvent.USER_NOTE_INPUT_VALIDATE)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .guard(guardService::validateNote)
                .action(generalActionService::handleUserNoteInput)
                .action(generalActionService::prepareTimeRecord)
                .action(createReportActionService::requestAdditionalReport)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.CONFIRM_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .action(createReportActionService::sendCategoryButtons)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.DECLINE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(createReportActionService::requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_DIALOG)
                .action(createReportActionService::persistReport)
                .action(generalActionService::sendRootMenuButtons)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.DECLINE_CREATION_FINAL_REPORT)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(generalActionService::declinePersistReport)
                .action(generalActionService::sendRootMenuButtons);


        // Handling CANCEL button
//                .and().withExternal()
//                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
//                .event(MessageEvent.CANCEL)
//                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())

//                .and().withExternal()
//                .source(CreateReportState.USER_TIME_INPUTTING)
//                .event(MessageEvent.CANCEL)
//                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())


        // Handling RETURN_TO_MAIN_MENU button
//                .and().withExternal()
//                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
//                .target(CreateReportState.END_DIALOG)
//                .event(MessageEvent.RETURN_TO_MAIN_MENU);
//                .action(reservedAction(), errorAction());
    }
}
