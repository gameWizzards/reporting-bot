package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.create_report.action.EndDialogAction;
import com.telegram.reporting.dialogs.create_report.action.HandleDateCategoryAction;
import com.telegram.reporting.dialogs.create_report.action.HandleUserDateInputAction;
import com.telegram.reporting.dialogs.create_report.action.HandleUserTimeInputAction;
import com.telegram.reporting.dialogs.create_report.action.RequestAdditionalReportAction;
import com.telegram.reporting.dialogs.create_report.action.RequestConfirmationReport;
import com.telegram.reporting.dialogs.create_report.action.RequestInputDateAction;
import com.telegram.reporting.dialogs.create_report.action.RequestInputTimeAction;
import com.telegram.reporting.dialogs.create_report.action.SendCategoryButtonsAction;
import com.telegram.reporting.dialogs.create_report.guard.ValidateDateGuard;
import com.telegram.reporting.dialogs.create_report.guard.ValidateTimeGuard;
import com.telegram.reporting.messages.MessageEvent;
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


    private final RequestInputDateAction requestInputDateAction;
    private final HandleUserDateInputAction handleUserDateInputAction;
    private final SendCategoryButtonsAction sendCategoryButtonsAction;
    private final ValidateDateGuard validateDateGuard;
    private final HandleDateCategoryAction handleDateCategoryAction;
    private final RequestInputTimeAction requestInputTimeAction;
    private final ValidateTimeGuard validateTimeGuard;
    private final HandleUserTimeInputAction handleUserTimeInputAction;
    private final RequestAdditionalReportAction requestAdditionalReportAction;
    private final RequestConfirmationReport requestConfirmationReport;
    private final EndDialogAction endDialogAction;

    @Autowired
    public CreateReportDialogStateMachineFactory(@Lazy RequestInputDateAction requestInputDateAction, @Lazy HandleUserDateInputAction handleUserDateInputAction,
                                                 @Lazy SendCategoryButtonsAction sendCategoryButtonsAction, @Lazy ValidateDateGuard validateDateGuard,
                                                 @Lazy HandleDateCategoryAction handleDateCategoryAction, @Lazy RequestInputTimeAction requestInputTimeAction,
                                                 @Lazy ValidateTimeGuard validateTimeGuard, @Lazy HandleUserTimeInputAction handleUserTimeInputAction,
                                                 @Lazy RequestAdditionalReportAction requestAdditionalReportAction, @Lazy RequestConfirmationReport requestConfirmationReport,
                                                 @Lazy EndDialogAction endDialogAction) {
        this.requestInputDateAction = requestInputDateAction;
        this.handleUserDateInputAction = handleUserDateInputAction;
        this.sendCategoryButtonsAction = sendCategoryButtonsAction;
        this.validateDateGuard = validateDateGuard;
        this.handleDateCategoryAction = handleDateCategoryAction;
        this.requestInputTimeAction = requestInputTimeAction;
        this.validateTimeGuard = validateTimeGuard;
        this.handleUserTimeInputAction = handleUserTimeInputAction;
        this.requestAdditionalReportAction = requestAdditionalReportAction;
        this.requestConfirmationReport = requestConfirmationReport;
        this.endDialogAction = endDialogAction;
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
                .event(MessageEvent.CREATE_REPORT_EVENT) //handle button
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(requestInputDateAction) //errorAction()) вернуть в бот текст введите дату отчета

                .and().withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.USER_DATE_INPUT_VALIDATE) //handle user input
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .guard(validateDateGuard)
                .action(handleUserDateInputAction)
                .action(sendCategoryButtonsAction)

                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.CHOICE_REPORT_CATEGORY)
                .target(CreateReportState.USER_TIME_INPUTTING)
                .action(handleDateCategoryAction)
                .action(requestInputTimeAction)

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.USER_TIME_INPUT_VALIDATE)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .guard(validateTimeGuard)
                .action(handleUserTimeInputAction)
                .action(requestAdditionalReportAction)
                // TODO add step - "Additional info"

//TODO add handling multi reporting
                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.CONFIRM_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .action(sendCategoryButtonsAction)

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.DECLINE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .action(requestConfirmationReport)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_DIALOG)
                .action(endDialogAction)

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.DECLINE_CREATION_FINAL_REPORT)
                .target(CreateReportState.USER_DATE_INPUTTING)
                .action(endDialogAction);


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
