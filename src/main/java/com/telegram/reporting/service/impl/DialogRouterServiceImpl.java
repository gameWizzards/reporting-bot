package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.create_report.CreateReportStateMachineHandlerImpl;
import com.telegram.reporting.dialogs.delete_report.DeleteReportStateMachineHandlerImpl;
import com.telegram.reporting.dialogs.update_report.UpdateReportStateMachineHandlerImpl;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DialogRouterServiceImpl implements DialogRouterService {
    Map<Long, StateMachineHandler> stateMachineHandlers = new HashMap<>();

    @PostConstruct
    public void init() {
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        MessageEvent messageEvent = MessageEvent.getByMessage(input);
        Long chatId = TelegramUtils.getChatId(update);

        if (messageEvent != null) {

            // event to create state machine
            if (MessageEvent.getStartDialogMessages().contains(messageEvent)) {
                switch (messageEvent) {
                    case CREATE_REPORT_EVENT -> stateMachineHandlers.put(chatId, new CreateReportStateMachineHandlerImpl());
                    case UPDATE_REPORT_EVENT -> stateMachineHandlers.put(chatId, new UpdateReportStateMachineHandlerImpl());
                    case DELETE_REPORT_EVENT -> stateMachineHandlers.put(chatId, new DeleteReportStateMachineHandlerImpl());
                   //... init all dialog state machine handlers
                }
                //TODO create state machine for user

                // event to update state machine
            } else {

                stateMachineHandlers.get(chatId).handleMessageEvent(messageEvent);

            }
            //TODO get state machine, or else create

            //TODO get from state dialog
//            handlers.get(messageEvent).handleMessageEvent(dialog, messageEvent);
            // user input
        } else {
            //TODO get from state StateMachineHandler
            stateMachineHandlers.get(chatId).handleUserInput(input);
        }

    }


}
