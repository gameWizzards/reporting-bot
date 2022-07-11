package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.general.delete_report.DeleteReportState;
import com.telegram.reporting.dialogs.MessageEvent;
import org.springframework.statemachine.StateContext;

public interface DeleteReportActionService {

    void requestDeleteConfirmation(StateContext<DeleteReportState, MessageEvent> context);

    void removeTimeRecord(StateContext<DeleteReportState, MessageEvent> context);
}
