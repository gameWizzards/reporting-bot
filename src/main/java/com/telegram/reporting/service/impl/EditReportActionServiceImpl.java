package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.edit_dialog.EditReportState;
import com.telegram.reporting.exception.MismatchButtonValueException;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.EditReportActionService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import com.telegram.reporting.utils.TimeRecordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class EditReportActionServiceImpl implements EditReportActionService {
    private final SendBotMessageService sendBotMessageService;
    private final CategoryService categoryService;
    private final TimeRecordService timeRecordService;

    public EditReportActionServiceImpl(SendBotMessageService sendBotMessageService, CategoryService categoryService,
                                       TimeRecordService timeRecordService) {
        this.sendBotMessageService = sendBotMessageService;
        this.categoryService = categoryService;
        this.timeRecordService = timeRecordService;
    }

    @Override
    public void requestChooseEditData(StateContext<EditReportState, MessageEvent> context) {
        String chatId = TelegramUtils.currentChatId(context);
        String editTimeRecordJson = TelegramUtils.getContextVariableValue(context, ContextVariable.TARGET_TIME_RECORD_JSON);
        String timeRecordsJson = TelegramUtils.getContextVariableValue(context, ContextVariable.TIME_RECORDS_JSON);

        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordMessage = TimeRecordUtils.convertTimeRecordToMessage(trTO);

        String message = """
                Выбери данные из этого отчета которые ты хочешь изменить.
                                
                  %s
                  
                """.formatted(timeRecordMessage);

        SendMessage sendMessage = new SendMessage(chatId, message);

        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.CATEGORY.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(ButtonValue.SPEND_TIME.text(), ButtonValue.NOTE.text());

        boolean existAvailableCategories = !CollectionUtils.isEmpty(KeyboardUtils.getAvailableButtons(timeRecordsJson));
        if (existAvailableCategories) {
            sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow, secondRow));
        } else {
            String tipMessage = """
                    ВАЖНО! IMPORTANT! ACHTUNG!
                    Ты не сможешь изменить категорию этого отчета, потому что на эту дату нет свободных категорий!
                                    
                    Измени необходимые данные в отчете этой категории, а также если нужно и в других отчетах!
                                        
                    При необходимости можешь удалить отчет, тогда его категория будет доступна для изменения!
                    """;
            sendBotMessageService.sendMessage(chatId, tipMessage);
            sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, secondRow));
        }
    }

    @Override
    public void sendDataToEdit(StateContext<EditReportState, MessageEvent> context) {
        String buttonValue = TelegramUtils.getContextVariableValue(context, ContextVariable.BUTTON_VALUE);
        String editTimeRecordJson = TelegramUtils.getContextVariableValue(context, ContextVariable.TARGET_TIME_RECORD_JSON);
        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String message;

        ButtonValue button = ButtonValue.getByText(buttonValue).orElseThrow(() -> new MismatchButtonValueException("Can't find appropriate enum of ButtonValue with value = " + buttonValue));

        switch (button) {
            case SPEND_TIME -> {
                message = """
                        Ты хочешь изменить "Затраченное время".
                                                        
                        Время в отчете = %s.
                                                        
                        Введи необходимое время:
                        """.formatted(trTO.getHours());

            }
            case CATEGORY -> {
                message = """
                        Ты хочешь изменить "Категорию отчета".
                                                
                        Текущая категория = "%s".
                                                
                        Выбери категорию из доступных:
                        """.formatted(trTO.getCategoryName());
            }
            case NOTE -> {
                message = """
                        Ты хочешь изменить "Примечание".
                                                
                        Текущее примечание = "%s".

                        Напиши новое примечание:
                        """.formatted(trTO.getNote());
            }
            default -> message = "Упс) что то пошло не так!";
        }

        String tip = """
                \n
                Если не хочешь менять данные, то введи произвольно. На шаге "Подтвердить изменения" ты сможешь все отменить!
                """;

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), message + tip);
    }

    @Override
    public void editTimeRecord(StateContext<EditReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        Optional<String> time = Optional.ofNullable(TelegramUtils.getContextVariableValue(context, ContextVariable.REPORT_TIME));
        Optional<String> note = Optional.ofNullable(TelegramUtils.getContextVariableValue(context, ContextVariable.REPORT_NOTE));
        Optional<String> categoryName = Optional.ofNullable(TelegramUtils.getContextVariableValue(context, ContextVariable.REPORT_CATEGORY_TYPE));

        String editTimeRecord = (String) variables.get(ContextVariable.TARGET_TIME_RECORD_JSON);

        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecord, TimeRecordTO.class);

        time.ifPresent(hours -> timeRecordTO.setHours(Integer.parseInt(hours)));
        note.ifPresent(timeRecordTO::setNote);
        categoryName.ifPresent(timeRecordTO::setCategoryName);

        // required to clear variables for next edit data
        variables.remove(ContextVariable.REPORT_TIME);
        variables.remove(ContextVariable.REPORT_NOTE);
        variables.remove(ContextVariable.REPORT_CATEGORY_TYPE);

        variables.put(ContextVariable.TARGET_TIME_RECORD_JSON, JsonUtils.serializeItem(timeRecordTO));
    }

    @Override
    public void requestEditAdditionalData(StateContext<EditReportState, MessageEvent> context) {
        String editTimeRecordJson = TelegramUtils.getContextVariableValue(context, ContextVariable.TARGET_TIME_RECORD_JSON);
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordToMessage = TimeRecordUtils.convertTimeRecordToMessage(timeRecordTO);
        String message = """
                Хочешь еще что нибудь изменить в этом отчете?
                                
                %s
                                
                """.formatted(timeRecordToMessage);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), message);
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.CONFIRM_EDIT_ADDITIONAL_DATA.text(), ButtonValue.DECLINE_EDIT_ADDITIONAL_DATA.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void sendCategoryButtons(StateContext<EditReportState, MessageEvent> context) {
        String timeRecordsJson = TelegramUtils.getContextVariableValue(context, ContextVariable.TIME_RECORDS_JSON);

        String chatId = TelegramUtils.currentChatId(context);

        List<String> buttons = KeyboardUtils.getAvailableButtons(timeRecordsJson);

        SendMessage sendMessage = new SendMessage(chatId, Message.CHOICE_REPORT_CATEGORY.text());
        KeyboardRow[] buttonsWithRows = KeyboardUtils.createButtonsWithRows(buttons, 2);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, buttonsWithRows));
    }

    @Override
    public void requestSaveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context) {
        String editTimeRecordJson = (String) context.getExtendedState().getVariables().get(ContextVariable.TARGET_TIME_RECORD_JSON);
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordToMessage = TimeRecordUtils.convertTimeRecordToMessage(timeRecordTO);
        String message = """
                Хочешь сохранить изменения для этого отчета?
                                
                %s
                                
                """.formatted(timeRecordToMessage);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), message);
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.APPLY_DATA_CHANGES.text(), ButtonValue.CANCEL.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void saveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context) {
        String editTimeRecord = TelegramUtils.getContextVariableValue(context, ContextVariable.TARGET_TIME_RECORD_JSON);
        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecord, TimeRecordTO.class);
        TimeRecord timeRecord = timeRecordService.getById(trTO.getId());

        timeRecord.setHours(trTO.getHours());
        timeRecord.setNote(trTO.getNote());
        Optional<Category> category = categoryService.getByName(trTO.getCategoryName());
        category.ifPresent(timeRecord::setCategory);


        timeRecordService.save(timeRecord);
        context.getExtendedState().getVariables().remove(ContextVariable.TARGET_TIME_RECORD_JSON);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Отчет успешно сохранен!");
    }

    @Override
    public void requestEditAdditionalTimeRecord(StateContext<EditReportState, MessageEvent> context) {
        String date = TelegramUtils.getContextVariableValue(context, ContextVariable.DATE);
        String message = """         
                Хочешь изменить еще один отчет за - %s?
                                
                """.formatted(date);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), message);
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.YES.text(), ButtonValue.NO.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }
}
