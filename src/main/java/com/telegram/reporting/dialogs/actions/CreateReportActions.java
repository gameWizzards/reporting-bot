package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.general.create_report.CreateReportState;
import org.springframework.statemachine.StateContext;

public interface CreateReportActions {
    void requestInputDate(StateContext<CreateReportState, MessageEvent> context);

    void sendExistedTimeRecords(StateContext<CreateReportState, MessageEvent> context);

    void sendCategoryButtons(StateContext<CreateReportState, MessageEvent> context);

    void requestInputTime(StateContext<CreateReportState, MessageEvent> context);

    void requestInputNote(StateContext<CreateReportState, MessageEvent> context);

    void prepareTimeRecord(StateContext<CreateReportState, MessageEvent> context);

    void requestAdditionalReport(StateContext<CreateReportState, MessageEvent> context);

    void requestConfirmationReport(StateContext<CreateReportState, MessageEvent> context);

    void persistReport(StateContext<CreateReportState, MessageEvent> context);

    void declinePersistReport(StateContext<CreateReportState, MessageEvent> context);

}
