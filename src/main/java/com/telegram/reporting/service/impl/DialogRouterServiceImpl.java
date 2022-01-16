package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.MessageEvent;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.impl.create_report.CreateReportStateMachineHandlerImpl;
import com.telegram.reporting.dialogs.impl.delete_report.DeleteReportStateMachineHandlerImpl;
import com.telegram.reporting.dialogs.impl.update_report.UpdateReportStateMachineHandlerImpl;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    private Map<MessageEvent, StateMachineHandler> startHandlers;

    private Map<Long, StateMachineHandler> stateMachineHandlers = new HashMap<>();

    @PostConstruct
    public void init() {
        startHandlers = new HashMap<>(3);
        startHandlers.put(MessageEvent.CREATE_REPORT_EVENT, new CreateReportStateMachineHandlerImpl());
        startHandlers.put(MessageEvent.UPDATE_REPORT_EVENT, new UpdateReportStateMachineHandlerImpl());
        startHandlers.put(MessageEvent.DELETE_REPORT_EVENT, new DeleteReportStateMachineHandlerImpl());
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.getChatId(update);

        MessageEvent messageEvent = MessageEvent.getByMessage(input);
        if (messageEvent != null) {
            if (startHandlers.containsKey(messageEvent)) {
                createStateMachineHandler(chatId, messageEvent);
            }
            stateMachineHandlers.get(chatId).handleMessageEvent(messageEvent);
        } else {
            stateMachineHandlers.get(chatId).handleUserInput(input);
        }
    }

    private void createStateMachineHandler(Long chatId, MessageEvent messageEvent) {
        if (stateMachineHandlers.containsKey(chatId)) {
            throw new IllegalStateException("Already has state machine for chat: " + chatId);
        }
        stateMachineHandlers.put(chatId, startHandlers.get(messageEvent));
    }
}
