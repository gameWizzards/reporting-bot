package com.telegram.reporting.dialogs;

import com.telegram.reporting.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.Objects;

@Slf4j
public class DefaultDialogListener<S, E> implements StateMachineListener<S, E> {
    private final StateMachine<S, E> stateMachine;

    public DefaultDialogListener(StateMachine<S, E> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void stateChanged(State<S, E> from, State<S, E> to) {
        if (Objects.nonNull(from)) {
            log.info("{} dialog go from {} to step {}",
                    CommonUtils.getLogPrefix(stateMachine),
                    from.getId(),
                    to.getId());
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
                CommonUtils.getLogPrefix(stateMachine),
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
    }

    @Override
    public void stateMachineStopped(StateMachine<S, E> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<S, E> stateMachine, Exception e) {
        log.error("{}. Current step = {}. Exception = {}",
                CommonUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                e);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        if (Objects.isNull(value)) {
            value = "Value_was_removed";
        }
        log.info("{} changed variable {} = {}",
                CommonUtils.getLogPrefix(stateMachine),
                key,
                value);
    }

    @Override
    public void stateContext(StateContext<S, E> stateContext) {

    }
}
