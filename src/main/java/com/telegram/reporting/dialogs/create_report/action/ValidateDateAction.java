package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.Optional;

public class ValidateDateAction implements Action<CreateReportState, MessageEvent> {
    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(MessageEvent.USER_DATE_INPUT.name());
        validateDate(userInput);

    }

    private boolean validateDate(String userInput) {
        if (userInput == null) {
            return false;
        }
//        userInput
        return true;
    }
}
