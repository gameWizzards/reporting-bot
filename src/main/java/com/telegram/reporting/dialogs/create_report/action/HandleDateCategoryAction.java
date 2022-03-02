package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HandleDateCategoryAction implements Action<CreateReportState, MessageEvent> {
    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.MESSAGE.name());
        context.getExtendedState().getVariables().put(ContextVariable.REPORT_CATEGORY_TYPE.name(), reportCategoryType);
    }
}
