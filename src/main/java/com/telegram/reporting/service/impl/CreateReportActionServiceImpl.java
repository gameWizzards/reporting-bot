package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.exception.MismatchCategoryException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.*;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDateTime;
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
    private final TimeRecordService timeRecordService;

    public CreateReportActionServiceImpl(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService,
                                         CategoryService categoryService, ReportService reportService,
                                         TimeRecordService timeRecordService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
        this.categoryService = categoryService;
        this.reportService = reportService;
        this.timeRecordService = timeRecordService;
    }

    @Override
    public void requestInputDate(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.USER_DATE_INPUT_CREATE_REPORT.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void sendExistedTimeRecords(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String chatId = TelegramUtils.currentChatId(context);
        String date = (String) variables.get(ContextVariable.DATE);

        List<TimeRecordTO> trTOs = timeRecordService.getTimeRecordTOs(date, chatId);

        if (CollectionUtils.isEmpty(trTOs)) {
            variables.put(ContextVariable.TIME_RECORDS_JSON, "");
            return;
        }

        String timeRecordMessage = MessageConvertorUtils.convertToMessage(trTOs);

        String message = """
                Ранее созданные отчеты за - %s.
                                
                 Отчеты: \n
                  %s
                ВАЖНО! IMPORTANT! ACHTUNG!
                Ты сможешь добавить только новые категории к этому отчету! Если хочешь изменить ранее созданные, тогда переходи в диалог "Изменить отчет".
                """.formatted(date, timeRecordMessage);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);
        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);

        sendBotMessageService.sendMessage(chatId, message);
    }


    @Override
    public void sendCategoryButtons(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage;
        String message;

        String chatId = TelegramUtils.currentChatId(context);
        String timeRecordsJson = (String) context.getExtendedState().getVariables().get(ContextVariable.TIME_RECORDS_JSON);
        List<String> buttons = KeyboardUtils.getAvailableCategoryButtons(timeRecordsJson);
        boolean isAllCategoriesOccupied = StringUtils.isNotBlank(timeRecordsJson) && buttons.isEmpty();

        if (isAllCategoriesOccupied) {
            CreateReportState currentState = context.getSource().getId();
            switch (currentState) {
                case USER_DATE_INPUTTING -> {
                    message = """
                            Все доступные категории на эту дату заполнены. Можешь выбрать новую дату или вернуться в главное меню.
                            """;
                    sendMessage = new SendMessage(chatId, message);
                    KeyboardRow button = KeyboardUtils.createButton(ButtonValue.INPUT_NEW_DATE.text());
                    sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, button));
                }
                case USER_CREATE_ADDITIONAL_REPORT -> {
                    message = """
                            Все доступные категории на эту дату заполнены. Можешь перейти к сохранению отчетов или пойти в главное меню.
                            """;
                    sendMessage = new SendMessage(chatId, message);
                    KeyboardRow button = KeyboardUtils.createButton(ButtonValue.LIST_TIME_RECORDS.text());
                    sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, button));
                }
            }
            return;
        }
        sendMessage = new SendMessage(chatId, Message.CHOICE_REPORT_CATEGORY.text());
        KeyboardRow[] buttonsWithRows = KeyboardUtils.createButtonsWithRows(buttons, 2);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, buttonsWithRows));
    }

    @Override
    public void requestInputTime(StateContext<CreateReportState, MessageEvent> context) {
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_CATEGORY_TYPE);

        String userMessageCategoryAccepted = """
                        Ты выбрал категорию отчета - "%s". Категория принята.
                                        
                        %s
                        """.formatted(reportCategoryType,
                Message.USER_TIME_INPUT.text());

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), userMessageCategoryAccepted);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void requestInputNote(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.REQUEST_ADD_NOTE_REPORT.text());
        KeyboardRow row = KeyboardUtils.createRowButtons(ButtonValue.SKIP_NOTE.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, row));
    }

    //TODO make check to limit of timeRecord for one report with AOP
    @Override
    public void prepareTimeRecord(StateContext<CreateReportState, MessageEvent> context) {
        List<TimeRecordTO> trTOs;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String time = (String) variables.get(ContextVariable.REPORT_TIME);
        String note = (String) variables.get(ContextVariable.REPORT_NOTE);
        String categoryName = (String) variables.get(ContextVariable.REPORT_CATEGORY_TYPE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        trTOs = StringUtils.isNotBlank(timeRecordJson)
                ? JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class)
                : new ArrayList<>();

        TimeRecordTO timeRecord = new TimeRecordTO();
        timeRecord.setHours(Integer.parseInt(time));
        timeRecord.setNote(note);
        timeRecord.setCategoryName(categoryName);
        timeRecord.setCreated(LocalDateTime.now());

        trTOs.add(timeRecord);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);

        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);
    }

    @Override
    public void requestAdditionalReport(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.REQUEST_ADDITIONAL_REPORT.text());
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.YES.text(), ButtonValue.NO.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void requestConfirmationReport(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.DATE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        String timeRecordMessage = trTOS.stream()
                .map(MessageConvertorUtils::convertToMessage)
                .collect(Collectors.joining("\n"));

        String message = """
                Хочешь отправить отчет за - %s.
                                
                 Отчеты: \n
                  %s
                                
                  %s
                """.formatted(date, timeRecordMessage, Message.REQUEST_CONFIRMATION_REPORT.text());

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), message);

        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.CONFIRM_CREATION_FINAL_REPORT.text(), ButtonValue.CANCEL.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void persistReport(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.DATE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);
        Long chatId = (Long) variables.get(ContextVariable.CHAT_ID);

        Report report = reportService.getReport(DateTimeUtils.parseDefaultDate(date), chatId);

        if (report == null) {
            report = new Report();

            Optional<User> user = telegramUserService.findByChatId(chatId);
            report.setDate(DateTimeUtils.parseDefaultDate(date));
            report.setUser(user.orElseThrow(() -> new TelegramUserException("Can't find user who's related with chatId = %s".formatted(chatId))));
        }

        report.setTimeRecords(convertToTimeRecordEntities(timeRecordJson, report));
        reportService.save(report);

        log.info("{} report saved - {}", variables.get(ContextVariable.LOG_PREFIX), report);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Отчет успешно создан!");
    }

    @Override
    public void declinePersistReport(StateContext<CreateReportState, MessageEvent> context) {
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Отправка отчета отменена!");
    }

    private List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report) {
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);
        List<TimeRecord> entities = new ArrayList<>();
        for (TimeRecordTO trTO : trTOS) {
            Optional<Category> categoryOptional = categoryService.getByName(trTO.getCategoryName());
            TimeRecord timeRecord = new TimeRecord(trTO);
            timeRecord.setId(trTO.getId());
            timeRecord.setCategory(categoryOptional.orElseThrow(() -> new MismatchCategoryException("Can't find category by name = %s".formatted(trTO.getCategoryName()))));
            timeRecord.setReport(report);
            entities.add(timeRecord);
        }
        return entities;
    }
}
