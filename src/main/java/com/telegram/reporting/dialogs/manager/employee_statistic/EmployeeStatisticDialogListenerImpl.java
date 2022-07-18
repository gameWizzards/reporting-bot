package com.telegram.reporting.dialogs.manager.employee_statistic;

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
public class EmployeeStatisticDialogListenerImpl implements StateMachineListener<EmployeeStatisticState, MessageEvent> {
    private StateMachine<EmployeeStatisticState, MessageEvent> stateMachine;

    @Override
    public void stateChanged(State<EmployeeStatisticState, MessageEvent> from, State<EmployeeStatisticState, MessageEvent> to) {
        if (from != null) {
            log.info("{} dialog go from {} to step {}", TelegramUtils.getLogPrefix(stateMachine), from.getId(), to.getId());
        }
    }

    @Override
    public void stateEntered(State<EmployeeStatisticState, MessageEvent> state) {

    }

    @Override
    public void stateExited(State<EmployeeStatisticState, MessageEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<MessageEvent> message) {
        log.error("{} Invalid transition! Current step = {}. Invalid event = {}.",
                TelegramUtils.getLogPrefix(stateMachine),
                stateMachine.getState().getId(),
                message.getPayload());
    }

    @Override
    public void transition(Transition<EmployeeStatisticState, MessageEvent> transition) {

    }

    @Override
    public void transitionStarted(Transition<EmployeeStatisticState, MessageEvent> transition) {

    }

    @Override
    public void transitionEnded(Transition<EmployeeStatisticState, MessageEvent> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<EmployeeStatisticState, MessageEvent> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void stateMachineStopped(StateMachine<EmployeeStatisticState, MessageEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<EmployeeStatisticState, MessageEvent> stateMachine, Exception e) {
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
    public void stateContext(StateContext<EmployeeStatisticState, MessageEvent> stateContext) {

    }
}
