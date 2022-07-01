package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    public final static String START_FLOW_MESSAGE = """
            Окей.
            Выбери диалог.
            """;
    private final Map<Long, StateMachineHandler> stateMachineHandlers;

    private final StateMachineHandler createReportHandler;
    private final StateMachineHandler deleteReportHandler;
    private final StateMachineHandler editReportHandler;
    private final StateMachineHandler statisticHandler;
    private final SendBotMessageService sendBotMessageService;

    public DialogRouterServiceImpl(@Qualifier("CreateReportStateMachineHandler") StateMachineHandler createReportHandler,
                                   @Qualifier("DeleteReportStateMachineHandler") StateMachineHandler deleteReportHandler,
                                   @Qualifier("EditReportStateMachineHandler") StateMachineHandler editReportHandler,
                                   @Qualifier("StatisticStateMachineHandler") StateMachineHandler statisticHandler,
                                   SendBotMessageService sendBotMessageService) {
        this.createReportHandler = createReportHandler;
        this.deleteReportHandler = deleteReportHandler;
        this.editReportHandler = editReportHandler;
        this.statisticHandler = statisticHandler;
        this.sendBotMessageService = sendBotMessageService;
        stateMachineHandlers = new HashMap<>();
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.currentChatId(update);

        Optional<ButtonValue> messageOptional = ButtonValue.getByText(input);
        if (messageOptional.isPresent()) {
            ButtonValue buttonValue = messageOptional.get();

            // return to root menu when click 'main menu' button
            if (ButtonValue.MAIN_MENU.equals(buttonValue)) {
                startFlow(chatId.toString());
                return;
            }

            // create new handler when buttonValue contains name of particular dialog
            if (ButtonValue.startDialogButtons().contains(buttonValue)) {
                createStateMachineHandler(chatId, buttonValue);
            }


            // when dialog in telegram remain on some step with buttons but app was reloaded
            // that means that there is no handler for the dialog - start from root menu
            if (!stateMachineHandlers.containsKey(chatId)) {
                sendBotMessageService.sendMessage(chatId, Message.GENERAL_ERROR_MESSAGE.text());
                startFlow(chatId.toString());
                return;
            }

            stateMachineHandlers.get(chatId).handleMessage(chatId, buttonValue);
        } else {

            // when dialog in telegram remain on some step with user input but app was reloaded
            // that means that is no handler for the dialog - start from root menu
            if (!stateMachineHandlers.containsKey(chatId)) {
                sendBotMessageService.sendMessage(chatId, Message.GENERAL_ERROR_MESSAGE.text());
                startFlow(chatId.toString());
                return;
            }
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    @Override
    public void startFlow(String chatId) {
        sendBotMessageService.sendMessageWithKeys(KeyboardUtils.createRootMenuMessage(chatId));
    }

    private StateMachineHandler createStateMachineHandler(Long chatId, ButtonValue buttonValue) {
        stateMachineHandlers.put(chatId, getStateMachineHandler(chatId, buttonValue));
        return stateMachineHandlers.get(chatId);
    }

    private StateMachineHandler getStateMachineHandler(Long chatId, ButtonValue buttonValue) {
        return switch (buttonValue) {
            case CREATE_REPORT_START_DIALOG -> createReportHandler.initStateMachine(chatId);
            case DELETE_REPORT_START_DIALOG -> deleteReportHandler.initStateMachine(chatId);
            case EDIT_REPORT_START_DIALOG -> editReportHandler.initStateMachine(chatId);
            case STATISTIC_START_DIALOG -> statisticHandler.initStateMachine(chatId);
            default -> null;
        };
    }
}
