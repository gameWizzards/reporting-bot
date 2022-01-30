package com.telegram.reporting.dialogs.delete_report;

import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class DeleteReportDialogListenerImpl implements StateMachineListener<DeleteReportState, MessageEvent> {

    @Override
    public void stateChanged(State<DeleteReportState, MessageEvent> state, State<DeleteReportState, MessageEvent> state1) {

    }

    @Override
    public void stateEntered(State<DeleteReportState, MessageEvent> state) {

    }

    @Override
    public void stateExited(State<DeleteReportState, MessageEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<MessageEvent> message) {
        log.error("Invalid transition! Invalid event {}.", message);
    }

    @Override
    public void transition(Transition<DeleteReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionStarted(Transition<DeleteReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionEnded(Transition<DeleteReportState, MessageEvent> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<DeleteReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineStopped(StateMachine<DeleteReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<DeleteReportState, MessageEvent> stateMachine, Exception e) {

    }

    @Override
    public void extendedStateChanged(Object o, Object o1) {

    }

    @Override
    public void stateContext(StateContext<DeleteReportState, MessageEvent> stateContext) {

    }
}
