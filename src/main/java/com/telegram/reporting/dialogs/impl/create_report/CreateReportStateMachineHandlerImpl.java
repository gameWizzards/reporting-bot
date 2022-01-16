package com.telegram.reporting.dialogs.impl.create_report;

import com.telegram.reporting.bot.MessageEvent;
import com.telegram.reporting.dialogs.StateMachineHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;

public class CreateReportStateMachineHandlerImpl implements StateMachineHandler {
    // not sure to init state machine like that, but maybe it's ok because state that machine has unique generic -> CreateReportState
    @Autowired
    private StateMachine<CreateReportState, MessageEvent> stateMachine;

    @Override
    public void handleMessageEvent(MessageEvent messageEvent) {
        stateMachine.sendEvent(messageEvent);
    }

    @Override
    public void handleUserInput(String userInput) {
        // check current state
        // choice event DATE_INPUT or TIME_INPUT
        // update state machine
        // send to action user input
    }
}

