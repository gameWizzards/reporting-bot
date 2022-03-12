package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.exception.MismatchCategoryException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class EndDialogAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final CategoryService categoryService;
    private final ReportService reportService;

    public EndDialogAction(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService,
                           CategoryService categoryService, ReportService reportService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
        this.categoryService = categoryService;
        this.reportService = reportService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.REPORT_DATE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);
        Long chatId = (Long) variables.get(ContextVariable.CHAT_ID);

        Optional<User> user = telegramUserService.findByChatId(chatId);

        Report report = new Report();
        report.setDate(DateTimeUtils.parseLocalDate(date));
        report.setUser(user.orElseThrow(() -> new TelegramUserException("Can't find user who's related with chatId = " + chatId)));

        report.setTimeRecords(convertToTimeRecordEntities(timeRecordJson, report));
        reportService.save(report);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Вы успешно создали отчет!");
    }

    private List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report) {
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);
        List<TimeRecord> entities = new ArrayList<>();
        for (TimeRecordTO trTO : trTOS) {
            Optional<Category> categoryOptional = categoryService.getByName(trTO.getCategoryName());
            TimeRecord timeRecord = new TimeRecord(trTO);
            timeRecord.setCategory(categoryOptional.orElseThrow(() -> new MismatchCategoryException("Can't find category by name = " + trTO.getCategoryName())));
            timeRecord.setReport(report);
            entities.add(timeRecord);
        }
        return entities;
    }
}
