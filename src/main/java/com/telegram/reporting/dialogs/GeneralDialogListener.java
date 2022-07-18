package com.telegram.reporting.dialogs;

import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class GeneralDialogListener<S, E> implements StateMachineListener<S, E> {
    private StateMachine<S, E> stateMachine;

    @Override
    public void stateChanged(State<S, E> from, State<S, E> to) {
        if (from != null) {
            log.info("{} dialog go from {} to step {}", TelegramUtils.getLogPrefix(stateMachine), from.getId(), to.getId());
        }
    }

    @Override
    public void stateEntered(State<S, E> state) {

    }

    @Override
    public void stateExited(State<S, E> state) {

    }

    @Override
    public void eventNotAccepted(Message<E> message) {
        log.error("{} Invalid transition! Current step = {}. Invalid event = {}.",
                TelegramUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                message.getPayload());
    }

    @Override
    public void transition(Transition<S, E> transition) {

    }

    @Override
    public void transitionStarted(Transition<S, E> transition) {

    }

    @Override
    public void transitionEnded(Transition<S, E> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<S, E> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void stateMachineStopped(StateMachine<S, E> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<S, E> stateMachine, Exception e) {
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
    public void stateContext(StateContext<S, E> stateContext) {

    }
}
