package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.delete_report.DeleteReportState;
import com.telegram.reporting.messages.MessageEvent;
import org.springframework.statemachine.StateContext;

public interface DeleteReportActionService {
    void requestInputDate(StateContext<DeleteReportState, MessageEvent> context);

    void requestDeleteConfirmation(StateContext<DeleteReportState, MessageEvent> context);

    void removeTimeRecord(StateContext<DeleteReportState, MessageEvent> context);
}
