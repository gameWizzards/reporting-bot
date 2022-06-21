package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
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

        Optional<Message> messageOptional = Message.getByText(input);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();

            if (Message.startMessages().contains(message)) {
                createStateMachineHandler(chatId, message, telegramNickname);
            }

            if (Message.MAIN_MENU.equals(message)) {
                startFlow(chatId.toString());
            }
            stateMachineHandlers.get(chatId).handleMessage(chatId, message);
        } else {
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    @Override
    public void startFlow(String chatId) {
        sendBotMessageService.sendMessageWithKeys(KeyboardUtils.createRootMenuMessage(chatId));
    }

    private StateMachineHandler createStateMachineHandler(Long chatId, Message message, String telegramNickname) {
        stateMachineHandlers.put(chatId, getStateMachineHandler(chatId, message, telegramNickname));
        return stateMachineHandlers.get(chatId);
    }

    private StateMachineHandler getStateMachineHandler(Long chatId, Message message, String telegramNickname) {
        return switch (message) {
            case CREATE_REPORT_START_MESSAGE -> createReportHandler.initStateMachine(chatId, telegramNickname);
            case DELETE_REPORT_START_MESSAGE -> deleteReportHandler.initStateMachine(chatId, telegramNickname);
            default -> null;
        };
    }
}
