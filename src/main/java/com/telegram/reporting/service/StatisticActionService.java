package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.general.statistic.StatisticState;
import org.springframework.statemachine.StateContext;

public interface StatisticActionService {
    void sendMonthStatistic(StateContext<StatisticState, MessageEvent> context);

    void sendPreviousMonthStatisticButton(StateContext<StatisticState, MessageEvent> context);

    void preparePreviousMonthDate(StateContext<StatisticState, MessageEvent> context);
}
