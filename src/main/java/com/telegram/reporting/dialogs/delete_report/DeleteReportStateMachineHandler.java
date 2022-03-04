package com.telegram.reporting.dialogs.delete_report;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("DeleteReportStateMachineHandler")
public class DeleteReportStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<DeleteReportState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<DeleteReportState, MessageEvent>> stateMachines;


    public DeleteReportStateMachineHandler(StateMachineFactory<DeleteReportState, MessageEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, Message message) {
        StateMachine<DeleteReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        log.info("Delete report handler do [handle message]. Bean reference: [{}]", this);
        log.info("DeleteStateMachine reference: [{}]", stateMachine.getUuid());
//        log.info("CreateStateMachine reference: [{}]", stateMachineCreate.getUuid());

//        stateMachine.sendEvent()
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        StateMachine<DeleteReportState, MessageEvent> stateMachine = stateMachines.get(chatId);
        log.info("Delete report handler do [handle USER input]. Bean reference: [{}]", this);
        // check current state
        // choice event DATE_INPUT or TIME_INPUT
        // update state machine
        // send to action user input
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        stateMachines.put(chatId, stateMachineFactory.getStateMachine());
        return this;
    }
}

