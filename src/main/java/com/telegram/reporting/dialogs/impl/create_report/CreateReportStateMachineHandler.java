package com.telegram.reporting.dialogs.impl.create_report;

import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.messages.Message;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateReportStateMachineHandler extends StateMachineHandler {

    // not sure to init state machine like that, but maybe it's ok because state that machine has unique generic -> CreateReportState
    //    @Autowired
    //    private StateMachine<CreateReportState, MessageEvent> stateMachine;
    //miha: not working, got null

    @Override
    public void handleMessage(Message message) {
    }

    @Override
    public void handleUserInput(String userInput) {
        // check current state
        // choice event DATE_INPUT or TIME_INPUT
        // update state machine
        // send to action user input
    }
}

