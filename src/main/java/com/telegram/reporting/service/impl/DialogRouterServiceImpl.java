package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    private final Map<Long, StateMachineHandler> stateMachineHandlers = new HashMap<>();

    @Autowired
    @Qualifier("CreateReportStateMachineHandler")
    private StateMachineHandler createReportHandler;

    @Autowired
    @Qualifier("DeleteReportStateMachineHandler")
    private StateMachineHandler deleteReportHandler;

    @Autowired
    private SendBotMessageService sendBotMessageService;

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

    @Override
    public void handleBeginningBotDialog(String commandIdentifier, String username, Update update) {
        sendBotMessageService.sendCommand(commandIdentifier, username, update);
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
