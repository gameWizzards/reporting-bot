package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.impl.create_report.CreateReportStateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    private Map<Message, StateMachineHandler> startHandlers;

    private final Map<Long, StateMachineHandler> stateMachineHandlers = new HashMap<>();

    @Autowired
    private CreateReportStateMachineHandler createReportStateMachineHandler;

    @Autowired
    @Qualifier("CreateReportStateMachineHandlerImpl")
    private StateMachineHandler createReportHandler;

    @PostConstruct
    public void init() {
        startHandlers = new HashMap<>(1);
        startHandlers.put(Message.CREATE_REPORT, createReportStateMachineHandler);
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.getChatId(update);

        Optional<Message> messageOptional = Message.getByText(input);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();

            if (startHandlers.containsKey(message)) {
                createStateMachineHandler(chatId, message).setChatId(chatId);
            }
            stateMachineHandlers.get(chatId).handleMessage(message);
        } else {
            //TODO get from state StateMachineHandler
            stateMachineHandlers.get(chatId).handleUserInput(input);
        }

    }

    private StateMachineHandler createStateMachineHandler(Long chatId, Message message) {
        if (stateMachineHandlers.containsKey(chatId)) {
            throw new IllegalStateException("Already has state machine for chat: " + chatId);
        }
        return stateMachineHandlers.put(chatId, startHandlers.get(message));
    }
}
