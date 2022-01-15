package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.messages.MessageEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CreateReportDialogStateMachineConfTest {

    @Autowired
    private StateMachine<CreateReportState, MessageEvent> stateMachine;

    @Test
    public void testInitStateMachine() {
        assertNotNull(stateMachine);
        assertEquals(stateMachine.getState().getId(), CreateReportState.USER_DATE_INPUTTING);
    }

    @Test
    public void testGreenWay() {
        stateMachine.sendEvent(MessageEvent.USER_DATE_INPUT);
        stateMachine.sendEvent(MessageEvent.VALID_DATE);
        stateMachine.sendEvent(MessageEvent.CHOICE_REPORT_CATEGORY);
        stateMachine.sendEvent(MessageEvent.USER_TIME_INPUT);
        stateMachine.sendEvent(MessageEvent.VALID_TIME);
        stateMachine.sendEvent(MessageEvent.DECLINE_ADDITIONAL_REPORT);
        stateMachine.sendEvent(MessageEvent.CONFIRM_CREATION_FINAL_REPORT);

        assertEquals(stateMachine.getState().getId(), CreateReportState.END_DIALOG);
    }
}