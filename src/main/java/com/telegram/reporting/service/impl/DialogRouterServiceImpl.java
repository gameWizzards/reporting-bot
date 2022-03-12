package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DialogRouterServiceImpl implements DialogRouterService {
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

        Optional<Message> messageOptional = Message.getByText(input);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();

            if (Message.startMessages().contains(message)) {
                createStateMachineHandler(chatId, message);
            }
            stateMachineHandlers.get(chatId).handleMessage(chatId, message);
        } else {
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    public final static String START_FLOW_MESSAGE = """
            Окей.
            Выбери диалог.
            """;

    @Override
    public void startFlow(User user) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId().toString());
        message.setText(START_FLOW_MESSAGE);

        KeyboardRow firstRow = KeyboardUtils.createButton(Message.CREATE_REPORT.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(Message.UPDATE_REPORT.text(), Message.DELETE_REPORT.text());
        sendBotMessageService.sendMessageWithKeys(message, KeyboardUtils.createKeyboardMarkup(firstRow, secondRow));
    }

    private StateMachineHandler createStateMachineHandler(Long chatId, Message message) {
        stateMachineHandlers.put(chatId, getStateMachineHandler(chatId, message));
        return stateMachineHandlers.get(chatId);
    }

    private StateMachineHandler getStateMachineHandler(Long chatId, Message message) {
        return switch (message) {
            case CREATE_REPORT -> createReportHandler.initStateMachine(chatId);
            case DELETE_REPORT -> deleteReportHandler.initStateMachine(chatId);
            default -> null;
        };
    }
}
