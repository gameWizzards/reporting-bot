package com.telegram.reporting.dialogs.statistic;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.service.GeneralActionService;
import com.telegram.reporting.service.GuardService;
import com.telegram.reporting.service.StatisticActionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "StatisticDialogStateMachineFactory")
public class StatisticDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<StatisticState, MessageEvent> {
    private final GuardService guardService;
    private final GeneralActionService generalActionService;
    private final StatisticActionService statisticActionService;

    public StatisticDialogStateMachineFactory(@Lazy GuardService guardService,
                                              @Lazy GeneralActionService generalActionService,
                                              @Lazy StatisticActionService statisticActionService) {
        this.guardService = guardService;
        this.generalActionService = generalActionService;
        this.statisticActionService = statisticActionService;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<StatisticState, MessageEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new StatisticDialogListenerImpl())
                // Start after creation
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<StatisticState, MessageEvent> states) throws Exception {
        states.withStates()
                .initial(StatisticState.START_STATISTIC_DIALOG)
                .end(StatisticState.END_STATISTIC_DIALOG)
                .states(EnumSet.allOf(StatisticState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<StatisticState, MessageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(StatisticState.START_STATISTIC_DIALOG)
                .event(MessageEvent.RUN_STATISTIC_DIALOG)
                .target(StatisticState.USER_STATISTIC_WATCH)
                .action(statisticActionService::sendMonthStatistic)
                .action(statisticActionService::sendPreviousMonthStatisticButton)

                .and().withExternal()
                .source(StatisticState.USER_STATISTIC_WATCH)
                .event(MessageEvent.SHOW_PREVIOUS_MONTH_STATISTIC)
                .target(StatisticState.USER_PREVIOUS_MONTH_STATISTIC_WATCH)
                .action(statisticActionService::preparePreviousMonthDate)
                .action(statisticActionService::sendMonthStatistic)

                .and().withExternal()
                .source(StatisticState.USER_PREVIOUS_MONTH_STATISTIC_WATCH)
                .event(MessageEvent.SHOW_PREVIOUS_MONTH_STATISTIC)
                .target(StatisticState.END_STATISTIC_DIALOG);
    }
}
