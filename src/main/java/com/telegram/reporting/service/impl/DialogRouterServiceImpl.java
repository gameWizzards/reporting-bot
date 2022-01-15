package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.create_report.CreateReportDialogStateMachineConf;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.dialogs.create_report.StateMachineHandler;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import liquibase.pro.packaged.S;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DialogRouterServiceImpl implements DialogRouterService {

    @PostConstruct
    public void init() {
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        MessageEvent messageEvent = MessageEvent.getByMessage(input);

        if (messageEvent != null) {
            if (MessageEvent.getStartDialogMessages().contains(messageEvent)) {
                Long chatId = TelegramUtils.getChatId(update);
                StateMachine<CreateReportState, MessageEvent> stateMachine;
                switch (messageEvent) {
                    case CREATE_REPORT_EVENT -> stateMachine = (StateMachine<CreateReportState, MessageEvent>) new CreateReportDialogStateMachineConf();
                    default -> stateMachine = (StateMachine<CreateReportState, MessageEvent>) new CreateReportDialogStateMachineConf();
                }
                stateMachine.getExtendedState().getVariables().put("CHAT_ID", chatId);

                //TODO create state machine for user
            }
            //TODO get state machine, or else create

            //TODO get from state dialog
//            handlers.get(messageEvent).handleMessageEvent(dialog, messageEvent);
        } else {
            //TODO get from state StateMachineHandler
        }

    }


}
