package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.messages.MessageEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "CreateReportDialogStateMachineFactory")
public class CreateReportDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<CreateReportState, MessageEvent> {

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
//                .action(reservedAction(), errorAction()) вернуть в бот текст введите дату отчета

                .and().withExternal()
                .source(CreateReportState.USER_DATE_INPUTTING)
                .event(MessageEvent.USER_DATE_INPUT)
                .target(CreateReportState.DATE_VALIDATION)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.DATE_VALIDATION)
                .event(MessageEvent.VALID_DATE)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
//                .guard(hideGuard())
//                .action(reservedAction(), errorAction()) вернуть в бот список категорий и текст выберите

                .and().withExternal()
                .source(CreateReportState.DATE_VALIDATION)
                .event(MessageEvent.INVALID_DATE)
                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.CHOICE_REPORT_CATEGORY)
                .target(CreateReportState.USER_TIME_INPUTTING)
//                .action(reservedAction(), errorAction()) обработать выбор категорий времени

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.USER_TIME_INPUT)
                .target(CreateReportState.TIME_VALIDATION)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.TIME_VALIDATION)
                .event(MessageEvent.VALID_TIME)
                .target(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
//                .guard(hideGuard())
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.TIME_VALIDATION)
                .event(MessageEvent.INVALID_TIME)
                .target(CreateReportState.USER_TIME_INPUTTING)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.CONFIRM_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_CREATE_ADDITIONAL_REPORT)
                .event(MessageEvent.DECLINE_ADDITIONAL_REPORT)
                .target(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.CONFIRM_CREATION_FINAL_REPORT)
                .target(CreateReportState.END_DIALOG)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_FINAL_REPORT_CONFIRMATION)
                .event(MessageEvent.DECLINE_CREATION_FINAL_REPORT)
                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())


            // Handling CANCEL button
                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .event(MessageEvent.CANCEL)
                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())

                .and().withExternal()
                .source(CreateReportState.USER_TIME_INPUTTING)
                .event(MessageEvent.CANCEL)
                .target(CreateReportState.USER_DATE_INPUTTING)
//                .action(reservedAction(), errorAction())


            // Handling RETURN_TO_MAIN_MENU button
                .and().withExternal()
                .source(CreateReportState.USER_DATE_CATEGORY_CHOOSE)
                .target(CreateReportState.END_DIALOG)
                .event(MessageEvent.RETURN_TO_MAIN_MENU);
//                .action(reservedAction(), errorAction());


    }
}
