package com.telegram.reporting.dialogs.general_dialogs.statistic;

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
public class StatisticDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<StatisticState, StatisticEvent> {
    private final StatisticActions statisticActions;

    public StatisticDialogStateMachineFactory(@Lazy StatisticActions statisticActions) {
        this.statisticActions = statisticActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<StatisticState, StatisticEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<StatisticState, StatisticEvent> states) throws Exception {
        states.withStates()
                .initial(StatisticState.START_STATISTIC_DIALOG)
                .end(StatisticState.END_STATISTIC_DIALOG)
                .states(EnumSet.allOf(StatisticState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<StatisticState, StatisticEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(StatisticState.START_STATISTIC_DIALOG)
                .event(StatisticEvent.RUN_STATISTIC_DIALOG)
                .target(StatisticState.USER_STATISTIC_WATCH)
                .action(statisticActions::sendMonthStatistic)
                .action(statisticActions::sendPreviousMonthStatisticButton)

                .and().withExternal()
                .source(StatisticState.USER_STATISTIC_WATCH)
                .event(StatisticEvent.SHOW_PREVIOUS_MONTH_STATISTIC)
                .target(StatisticState.USER_PREVIOUS_MONTH_STATISTIC_WATCH)
                .action(statisticActions::preparePreviousMonthDate)
                .action(statisticActions::sendMonthStatistic)
                .action(statisticActions::sendCurrentMonthStatisticButton)

                .and().withExternal()
                .source(StatisticState.USER_PREVIOUS_MONTH_STATISTIC_WATCH)
                .event(StatisticEvent.SHOW_CURRENT_MONTH_STATISTIC)
                .target(StatisticState.USER_STATISTIC_WATCH)
                .action(statisticActions::prepareCurrentMonthDate)
                .action(statisticActions::sendMonthStatistic)
                .action(statisticActions::sendPreviousMonthStatisticButton);
    }
}
