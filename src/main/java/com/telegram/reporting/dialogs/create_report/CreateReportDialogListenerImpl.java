package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class CreateReportDialogListenerImpl implements StateMachineListener<CreateReportState, MessageEvent> {
    private StateMachine<CreateReportState, MessageEvent> stateMachine;

    @Override
    public void stateChanged(State<CreateReportState, MessageEvent> from, State<CreateReportState, MessageEvent> to) {
        if (from != null) {
            log.info("{} dialog go from {} to step {}", TelegramUtils.getLogPrefix(stateMachine), from.getId(), to.getId());
        }
    }

    @Override
    public void stateEntered(State<CreateReportState, MessageEvent> state) {

    }

    @Override
    public void stateExited(State<CreateReportState, MessageEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<MessageEvent> message) {
        log.error("{} Invalid transition! Current step = {}. Invalid event {}.",
                TelegramUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                message);
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
        this.stateMachine = stateMachine;
    }

    @Override
    public void stateMachineStopped(StateMachine<CreateReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<CreateReportState, MessageEvent> stateMachine, Exception e) {
        log.error("{}. Current step = {}. Exception = {}",
                TelegramUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                e);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        if (value == null) {
            value = "Value_was_removed";
        }
        log.info("{} changed variable {} = {}",
                TelegramUtils.getLogPrefix(stateMachine),
                key,
                value);
    }

    @Override
    public void stateContext(StateContext<CreateReportState, MessageEvent> stateContext) {

    }
}
