package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.service.DialogHandler;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("GeneralDialogHandler")
public class GeneralDialogHandlerImpl implements DialogHandler {

    private final Map<String, StateMachineHandler> stateMachineHandlers;

    private final StateMachineHandler createReportHandler;
    private final StateMachineHandler deleteReportHandler;
    private final StateMachineHandler editReportHandler;
    private final StateMachineHandler statisticHandler;
    private final SendBotMessageService sendBotMessageService;

    public GeneralDialogHandlerImpl(@Qualifier("CreateReportStateMachineHandler") StateMachineHandler createReportHandler,
                                    @Qualifier("DeleteReportStateMachineHandler") StateMachineHandler deleteReportHandler,
                                    @Qualifier("EditReportStateMachineHandler") StateMachineHandler editReportHandler,
                                    @Qualifier("StatisticStateMachineHandler") StateMachineHandler statisticHandler,
                                    SendBotMessageService sendBotMessageService) {
        this.createReportHandler = createReportHandler;
        this.deleteReportHandler = deleteReportHandler;
        this.editReportHandler = editReportHandler;
        this.statisticHandler = statisticHandler;
        this.sendBotMessageService = sendBotMessageService;
        stateMachineHandlers = new ConcurrentHashMap<>();
    }

    @Override
    public void handleTelegramInput(String chatId, String input) {
        Optional<ButtonValue> optionalButtonValue = ButtonValue.getByText(input);

        if (optionalButtonValue.isPresent()) {
            ButtonValue buttonValue = optionalButtonValue.get();

            stateMachineHandlers.get(chatId).handleMessage(Long.parseLong(chatId), buttonValue);
        } else {

            stateMachineHandlers.get(chatId).handleUserInput(Long.parseLong(chatId), input);
        }
    }

    @Override
    public void createStateMachineHandler(String chatId, ButtonValue buttonValue) {

        StateMachineHandler handler = switch (buttonValue) {
            case CREATE_REPORT_START_DIALOG -> createReportHandler.initStateMachine(Long.parseLong(chatId));
            case DELETE_REPORT_START_DIALOG -> deleteReportHandler.initStateMachine(Long.parseLong(chatId));
            case EDIT_REPORT_START_DIALOG -> editReportHandler.initStateMachine(Long.parseLong(chatId));
            case STATISTIC_START_DIALOG -> statisticHandler.initStateMachine(Long.parseLong(chatId));
            default -> null;
        };
        stateMachineHandlers.put(chatId, handler);
    }

    @Override
    public List<KeyboardRow> getRootMenuButtons(String chatId) {
        return List.of(
                KeyboardUtils.createButton(ButtonValue.CREATE_REPORT_START_DIALOG.text()),
                KeyboardUtils.createRowButtons(ButtonValue.EDIT_REPORT_START_DIALOG.text(), ButtonValue.DELETE_REPORT_START_DIALOG.text()),
                KeyboardUtils.createButton(ButtonValue.STATISTIC_START_DIALOG.text()));
    }

    @Override
    public void removeStateMachineHandler(String chatId) {
        stateMachineHandlers.remove(chatId);
    }

    @Override
    public boolean belongToDialogStarter(ButtonValue buttonValue) {
        return List.of(ButtonValue.CREATE_REPORT_START_DIALOG, ButtonValue.EDIT_REPORT_START_DIALOG,
                        ButtonValue.DELETE_REPORT_START_DIALOG, ButtonValue.STATISTIC_START_DIALOG)
                .contains(buttonValue);
    }
}
