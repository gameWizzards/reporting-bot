package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.general.statistic.StatisticState;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.StatisticActionService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.MessageConvertorUtils;
import com.telegram.reporting.utils.Month;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticActionServiceImpl implements StatisticActionService {
    private final SendBotMessageService sendBotMessageService;
    private final ReportService reportService;

    public StatisticActionServiceImpl(SendBotMessageService sendBotMessageService,
                                      ReportService reportService) {
        this.sendBotMessageService = sendBotMessageService;
        this.reportService = reportService;
    }

    @Override
    @Transactional
    public void sendMonthStatistic(StateContext<StatisticState, MessageEvent> context) {
        AtomicInteger ordinalNumb = new AtomicInteger(1);

        int month = getStatisticMonth(context.getExtendedState().getVariables());
        List<Report> reports = reportService.getReportsBelongMonth(month, TelegramUtils.currentChatId(context));

        long sumHours = reports.parallelStream()
                .flatMap(repo -> repo.getTimeRecords().stream())
                .mapToLong(TimeRecord::getHours)
                .sum();

        String statistic = reports.stream()
                .map(MessageConvertorUtils::convertToStatisticMessage)
                .map(s -> ordinalNumb.getAndIncrement() + ". " + s)
                .collect(Collectors.joining());

        String statisticMessage = """
                Общее время за %s - %d ч.
                                
                %s
                """.formatted(Month.getNameByOrdinal(month), sumHours, statistic);

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
        int month = LocalDate.now().minusMonths(minusMonths).getMonthValue();
        context.getExtendedState().getVariables().put(ContextVariable.MONTH, month);
    }

    private int getStatisticMonth(Map<Object, Object> variables) {
        Optional<Integer> monthOpt = Optional.ofNullable((Integer) variables.remove(ContextVariable.MONTH));
        return monthOpt
                .orElse(LocalDate.now().getMonthValue());
    }
}
