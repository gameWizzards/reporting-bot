package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.impl.create_report.CreateReportStateMachineHandler;
import com.telegram.reporting.dialogs.impl.delete_report.DeleteReportStateMachineHandler;
import com.telegram.reporting.dialogs.impl.update_report.UpdateReportStateMachineHandler;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    private Map<Message, StateMachineHandler> startHandlers;

    private Map<Long, StateMachineHandler> stateMachineHandlers = new HashMap<>();

    @PostConstruct
    public void init() {
        startHandlers = new HashMap<>(3);
        startHandlers.put(Message.CREATE_REPORT, new CreateReportStateMachineHandler());
        startHandlers.put(Message.UPDATE_REPORT, new UpdateReportStateMachineHandler());
        startHandlers.put(Message.DELETE_REPORT, new DeleteReportStateMachineHandler());
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.getChatId(update);

        Optional<Message> messageOptional = Message.getByText(input);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();

            if (startHandlers.containsKey(message)) {
                createStateMachineHandler(chatId, message);
            }
            stateMachineHandlers.get(chatId).handleMessage(message);
        } else {
            stateMachineHandlers.get(chatId).handleUserInput(input);
        }
    }

    private void createStateMachineHandler(Long chatId, Message message) {
        if (stateMachineHandlers.containsKey(chatId)) {
            throw new IllegalStateException("Already has state machine for chat: " + chatId);
        }
        //TODO problems
        //1. need to init new instance for users
        //2. need to inject chatId to handler
        stateMachineHandlers.put(chatId, startHandlers.get(message));
    }
}
