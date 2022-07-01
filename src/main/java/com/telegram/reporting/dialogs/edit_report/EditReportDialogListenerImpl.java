package com.telegram.reporting.dialogs.edit_report;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class EditReportDialogListenerImpl implements StateMachineListener<EditReportState, MessageEvent> {
    private StateMachine<EditReportState, MessageEvent> stateMachine;
    @Override
    public void stateChanged(State<EditReportState, MessageEvent> from, State<EditReportState, MessageEvent> to) {
        if (from != null) {
            log.info("{} dialog go from {} to step {}", TelegramUtils.getLogPrefix(stateMachine), from.getId(), to.getId());
        }
    }

    @Override
    public void stateEntered(State<EditReportState, MessageEvent> state) {

    }

    @Override
    public void stateExited(State<EditReportState, MessageEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<MessageEvent> message) {
        log.error("{} Invalid transition! Current step = {}. Invalid event = {}.",
                TelegramUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                message.getPayload());
    }

    @Override
    public void transition(Transition<EditReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionStarted(Transition<EditReportState, MessageEvent> transition) {

    }

    @Override
    public void transitionEnded(Transition<EditReportState, MessageEvent> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<EditReportState, MessageEvent> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void stateMachineStopped(StateMachine<EditReportState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<EditReportState, MessageEvent> stateMachine, Exception e) {
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
    public void stateContext(StateContext<EditReportState, MessageEvent> stateContext) {

    }
}
