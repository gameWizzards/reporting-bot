package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import org.springframework.statemachine.StateContext;

public interface CreateReportActionService {
    void requestInputDate(StateContext<CreateReportState, MessageEvent> context);

    void sendCategoryButtons(StateContext<CreateReportState, MessageEvent> context);

    void handleCategory(StateContext<CreateReportState, MessageEvent> context);

    void requestInputTime(StateContext<CreateReportState, MessageEvent> context);

    void requestAdditionalReport(StateContext<CreateReportState, MessageEvent> context);

    void requestConfirmationReport(StateContext<CreateReportState, MessageEvent> context);

    void persistReport(StateContext<CreateReportState, MessageEvent> context);

}
