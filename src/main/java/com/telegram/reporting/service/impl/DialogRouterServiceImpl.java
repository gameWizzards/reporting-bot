package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.ButtonValue;
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
    private final SendBotMessageService sendBotMessageService;

    public DialogRouterServiceImpl(@Qualifier("CreateReportStateMachineHandler") StateMachineHandler createReportHandler,
                                   @Qualifier("DeleteReportStateMachineHandler") StateMachineHandler deleteReportHandler,
                                   SendBotMessageService sendBotMessageService) {
        this.createReportHandler = createReportHandler;
        this.deleteReportHandler = deleteReportHandler;
        this.sendBotMessageService = sendBotMessageService;
        stateMachineHandlers = new HashMap<>();
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.currentChatId(update);
        String telegramNickname = update.getMessage().getFrom().getUserName();

        Optional<ButtonValue> messageOptional = ButtonValue.getByText(input);
        if (messageOptional.isPresent()) {
            ButtonValue buttonValue = messageOptional.get();

            if (ButtonValue.startMessages().contains(buttonValue)) {
                createStateMachineHandler(chatId, buttonValue, telegramNickname);
            }

            if (ButtonValue.MAIN_MENU.equals(buttonValue)) {
                startFlow(chatId.toString());
            }
            stateMachineHandlers.get(chatId).handleMessage(chatId, buttonValue);
        } else {
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    @Override
    public void startFlow(String chatId) {
        sendBotMessageService.sendMessageWithKeys(KeyboardUtils.createRootMenuMessage(chatId));
    }

    private StateMachineHandler createStateMachineHandler(Long chatId, ButtonValue buttonValue, String telegramNickname) {
        stateMachineHandlers.put(chatId, getStateMachineHandler(chatId, buttonValue, telegramNickname));
        return stateMachineHandlers.get(chatId);
    }

    private StateMachineHandler getStateMachineHandler(Long chatId, ButtonValue buttonValue, String telegramNickname) {
        return switch (buttonValue) {
            case CREATE_REPORT_START_DIALOG -> createReportHandler.initStateMachine(chatId, telegramNickname);
            case DELETE_REPORT_START_DIALOG -> deleteReportHandler.initStateMachine(chatId, telegramNickname);
            default -> null;
        };
    }
}
