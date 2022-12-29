package com.telegram.reporting.dialogs.general_dialogs.statistic;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticActions {
    private final Map<Long, Map<LocalDate, String>> statisticMessagesCache = new ConcurrentHashMap<>();
    private final SendBotMessageService sendBotMessageService;
    private final ReportService reportService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18nMessageService;

    public void sendMonthStatistic(StateContext<StatisticState, StatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        LocalDate statisticDate = getStatisticMonth(CommonUtils.getContextVarAsString(context, ContextVarKey.DATE));

//        TODO remove run time statistic after check on prod server
        long startMillis = System.currentTimeMillis();
        if (statisticMessagesCache.containsKey(chatId) && statisticMessagesCache.get(chatId).containsKey(statisticDate)) {
            String statisticMessage = statisticMessagesCache.get(chatId).get(statisticDate);

            log.warn("Cached time response = {}", System.currentTimeMillis() - startMillis);
            sendBotMessageService.sendMessage(chatId, statisticMessage);
            return;
        }

        List<Report> reports = reportService.getReportsBelongMonth(statisticDate, chatId);

        String totalMonthStatisticMessage = i18nMessageService.createMonthStatisticMessage(chatId, statisticDate, reports);

        if (statisticMessagesCache.containsKey(chatId)) {
            statisticMessagesCache.get(chatId).put(statisticDate, totalMonthStatisticMessage);
        } else {
            Map<LocalDate, String> dateMessageCache = new HashMap<>();
            dateMessageCache.put(statisticDate, totalMonthStatisticMessage);
            statisticMessagesCache.put(chatId, dateMessageCache);
        }
        log.warn("Evaluated time response = {}", System.currentTimeMillis() - startMillis);
        sendBotMessageService.sendMessage(chatId, totalMonthStatisticMessage);
    }

    public void sendPreviousMonthStatisticButton(StateContext<StatisticState, StatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18nMessageService.getMessage(chatId, MessageKey.GS_REQUEST_PREVIOUS_MONTH_STATISTIC));

        sendBotMessageService.sendMessageWithKeys(sendMessage,
                i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.GS_PREVIOUS_MONTH_STATISTIC));
    }

    public void preparePreviousMonthDate(StateContext<StatisticState, StatisticEvent> context) {
        long minusMonths = 1;
        LocalDate previousMonth = LocalDate.now().minusMonths(minusMonths);
        context.getExtendedState().getVariables().put(ContextVarKey.DATE, DateTimeUtils.toDefaultFormat(previousMonth));
    }

    public void sendCurrentMonthStatisticButton(StateContext<StatisticState, StatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18nMessageService.getMessage(chatId, MessageKey.GS_REQUEST_CURRENT_MONTH_STATISTIC));

        sendBotMessageService.sendMessageWithKeys(sendMessage,
                i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.GS_CURRENT_MONTH_STATISTIC));
    }

    public void prepareCurrentMonthDate(StateContext<StatisticState, StatisticEvent> context) {
        context.getExtendedState().getVariables().put(ContextVarKey.DATE, DateTimeUtils.toDefaultFormat(LocalDate.now()));
    }


    private LocalDate getStatisticMonth(String statisticDate) {
        if (StringUtils.isBlank(statisticDate)) {
            return LocalDate.now();
        }
        return DateTimeUtils.parseDefaultDate(statisticDate);
    }

    @Async
    @EventListener(ClearStatisticCacheEvent.class)
    public void clearDialogCache(ClearStatisticCacheEvent event) {
        statisticMessagesCache.remove(event.chatId());
        log.info("Event [{}] is done for chat: [{}]", event.getClass().getSimpleName(), event.chatId());
    }
}
