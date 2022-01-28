package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component("CreateReportStateMachineHandlerImpl")
public class CreateReportStateMachineHandlerImpl implements StateMachineHandler {
    // not sure to init state machine like that, but maybe it's ok because state that machine has unique generic -> CreateReportState
    @Autowired
    private StateMachine<CreateReportState, MessageEvent> stateMachine;

    @Override
    public void handleMessageEvent(MessageEvent messageEvent) {
        stateMachine.sendEvent(MessageEvent.USER_DATE_INPUT);
    }

    @Override
    public void handleUserInput(String userInput) {
        stateMachine.sendEvent(MessageEvent.USER_DATE_INPUT);
        // check current state
        // choice event DATE_INPUT or TIME_INPUT
        // update state machine
        // send to action user input
    }
}

