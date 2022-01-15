package com.telegram.reporting.dialogs.listener;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class CreateReportDialogListenerImpl implements StateMachineListener<CreateReportState, MessageEvent> {

    @Override
    public void stateChanged(State<CreateReportState, MessageEvent> state, State<CreateReportState, MessageEvent> state1) {

    }

    @Override
    public void stateEntered(State<CreateReportState, MessageEvent> state) {

    }

    @Override
    public void stateExited(State<CreateReportState, MessageEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<MessageEvent> message) {
        log.error("Invalid transition! Invalid event {}.", message);
    }

    @Override
    public void transition(Transition<CreateReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionStarted(Transition<CreateReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionEnded(Transition<CreateReportState, MessageEvent> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<CreateReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineStopped(StateMachine<CreateReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<CreateReportState, MessageEvent> stateMachine, Exception e) {

    }

    @Override
    public void extendedStateChanged(Object o, Object o1) {

    }

    @Override
    public void stateContext(StateContext<CreateReportState, MessageEvent> stateContext) {

    }
}
