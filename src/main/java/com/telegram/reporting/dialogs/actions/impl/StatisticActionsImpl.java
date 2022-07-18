package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.general.statistic.StatisticState;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.dialogs.actions.StatisticActions;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticActionsImpl implements StatisticActions {
    private final SendBotMessageService sendBotMessageService;
    private final ReportService reportService;

    public StatisticActionsImpl(SendBotMessageService sendBotMessageService,
                                ReportService reportService) {
        this.sendBotMessageService = sendBotMessageService;
        this.reportService = reportService;
    }

    @Override
    public void sendMonthStatistic(StateContext<StatisticState, MessageEvent> context) {
        AtomicInteger ordinalNumb = new AtomicInteger(1);
        Map<String, Integer> categoryHours = new HashMap<>();

        LocalDate statisticDate = getStatisticMonth(TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE));
        List<Report> reports = reportService.getReportsBelongMonth(statisticDate, TelegramUtils.currentChatId(context));

        reports.parallelStream()
                .map(Report::getTimeRecords)
                .flatMap(Collection::stream)
                .forEach(tr -> categoryHours.put(tr.getCategory().getName(), categoryHours.getOrDefault(tr.getCategory().getName(), 0) + tr.getHours()));

        int sumHours = categoryHours.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        String categoryHoursMessage = MessageConvertorUtils.prepareHoursByCategoryMessage(categoryHours);

        String statistic = reports.stream()
                .map(MessageConvertorUtils::convertToStatisticMessage)
                .map(s -> ordinalNumb.getAndIncrement() + ". " + s)
                .collect(Collectors.joining());

        String statisticMessage = """
                Общее время за %s - %d ч.
                %s
                %s
                """.formatted(Month.getNameByOrdinal(statisticDate.getMonthValue()), sumHours, categoryHoursMessage, statistic);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatIdString(context), statisticMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void sendPreviousMonthStatisticButton(StateContext<StatisticState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatIdString(context), "Также доступна статистика за прошлый месяц.");
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.PREVIOUS_MONTH_STATISTIC.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void preparePreviousMonthDate(StateContext<StatisticState, MessageEvent> context) {
        long minusMonths = 1;
        LocalDate previousMonth = LocalDate.now().minusMonths(minusMonths);
        context.getExtendedState().getVariables().put(ContextVariable.DATE, DateTimeUtils.toDefaultFormat(previousMonth));
    }

    private LocalDate getStatisticMonth(String statisticDate) {
        if (StringUtils.isBlank(statisticDate)) {
            return LocalDate.now();
        }
        return DateTimeUtils.parseDefaultDate(statisticDate);
    }
}
