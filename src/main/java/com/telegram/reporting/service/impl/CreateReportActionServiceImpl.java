package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.exception.MismatchCategoryException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.*;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreateReportActionServiceImpl implements CreateReportActionService {
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final CategoryService categoryService;
    private final ReportService reportService;

    public CreateReportActionServiceImpl(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService,
                                         CategoryService categoryService, ReportService reportService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
        this.categoryService = categoryService;
        this.reportService = reportService;
    }

    @Override
    public void requestInputDate(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.USER_DATE_INPUT_CREATE_REPORT.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void sendCategoryButtons(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.CHOICE_REPORT_CATEGORY.text());
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(Message.REPORT_CATEGORY_ON_STORAGE.text(), Message.REPORT_CATEGORY_ON_ORDER.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(Message.REPORT_CATEGORY_ON_OFFICE.text(), Message.REPORT_CATEGORY_ON_COORDINATION.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow, secondRow));
    }

    @Override
    public void handleCategory(StateContext<CreateReportState, MessageEvent> context) {
        // TODO create method getContextVariable(ContextVariable var) in TGUtils
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.MESSAGE);
        context.getExtendedState().getVariables().put(ContextVariable.REPORT_CATEGORY_TYPE, reportCategoryType);
    }

    @Override
    public void requestInputTime(StateContext<CreateReportState, MessageEvent> context) {
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_CATEGORY_TYPE);

        String userMessageCategoryAccepted = String.format("""
                        Вы выбрали категорию отчета - "%s". Категория принята.
                                        
                        %s
                        """,
                reportCategoryType,
                Message.USER_TIME_INPUT.text());

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), userMessageCategoryAccepted);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void requestAdditionalReport(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.REQUEST_ADDITIONAL_REPORT.text());
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(Message.CONFIRM_ADDITIONAL_REPORT.text(), Message.DECLINE_ADDITIONAL_REPORT.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));

    }

    @Override
    public void requestConfirmationReport(StateContext<CreateReportState, MessageEvent> context) {

        String message = """
                Вы хотите отправить отчет за - %s.
                                
                 Отчеты: \n
                  %s
                                
                  %s
                """;

        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.DATE);

        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        String timeRecordMessage = trTOS.stream()
                .map(TimeRecordUtils::convertTimeRecordToMessage)
                .collect(Collectors.joining("\n"));

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), String.format(message, date, timeRecordMessage, Message.REQUEST_CONFIRMATION_REPORT.text()));

        KeyboardRow firstRow = KeyboardUtils.createRowButtons(Message.CONFIRM_CREATION_FINAL_REPORT.text(), Message.CANCEL.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void persistReport(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.DATE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);
        Long chatId = (Long) variables.get(ContextVariable.CHAT_ID);

        Optional<User> user = telegramUserService.findByChatId(chatId);

        Report report = new Report();
        report.setDate(DateTimeUtils.parseDefaultDate(date));
        report.setUser(user.orElseThrow(() -> new TelegramUserException("Can't find user who's related with chatId = " + chatId)));

        report.setTimeRecords(convertToTimeRecordEntities(timeRecordJson, report));
        reportService.save(report);

        log.info("{} report saved - {}", variables.get(ContextVariable.LOG_PREFIX), report);

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
