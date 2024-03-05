package com.telegram.reporting.dialogs.general_dialogs.statistic;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.domain.Report;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticActions {
    private final SendBotMessageService sendBotMessageService;
    private final ReportService reportService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18nMessageService;

    public void sendMonthStatistic(StateContext<StatisticState, StatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        LocalDate statisticDate = DateTimeUtils.getStatisticMonth(CommonUtils.getContextVarAsString(context, ContextVarKey.DATE));

        List<Report> reports = reportService.getReportsBelongMonth(statisticDate, chatId);
        String totalMonthStatisticMessage = i18nMessageService.createMonthStatisticMessage(chatId, statisticDate, reports);

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
}
