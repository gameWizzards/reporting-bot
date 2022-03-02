package com.telegram.reporting.dialogs.impl.create_report;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class CreateReportStateMachineHandlerTest {


    private StateMachine<CreateReportState, MessageEvent> stateMachine;
    @Autowired
    private StateMachineFactory<CreateReportState, MessageEvent> stateMachineFactory;


    @Test
    public void testInitStateMachine() {
        assertNotNull(stateMachine);
        assertEquals(stateMachine.getState().getId(), CreateReportState.USER_DATE_INPUTTING);
    }

    @Test
    public void testGreenWay() {
        stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.sendEvent(MessageEvent.USER_DATE_INPUT_VALIDATE);
        stateMachine.sendEvent(MessageEvent.VALID_DATE);
        stateMachine.sendEvent(MessageEvent.CHOICE_REPORT_CATEGORY);
        stateMachine.sendEvent(MessageEvent.USER_TIME_INPUT_VALIDATE);
        stateMachine.sendEvent(MessageEvent.VALID_TIME);
        stateMachine.sendEvent(MessageEvent.DECLINE_ADDITIONAL_REPORT);
        stateMachine.sendEvent(MessageEvent.CONFIRM_CREATION_FINAL_REPORT);

        assertEquals(stateMachine.getState().getId(), CreateReportState.END_DIALOG);
    }

    @Test
    public void testGenerateKeyboardRows() {
//        ReplyKeyboardMarkup keyboardMarkup = KeyboardUtils.createKeyboardMarkup(3, "1", "2", "3", "4");

//        for (KeyboardRow keyboardButtons : keyboardMarkup.getKeyboard()) {
//            log.info("Key = " +keyboardButtons );
//        }

    }
}
