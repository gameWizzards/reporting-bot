package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.general.edit_report.EditReportState;
import org.springframework.statemachine.StateContext;

public interface EditReportActions {

    void requestChooseEditData(StateContext<EditReportState, MessageEvent> context);

    void sendDataToEdit(StateContext<EditReportState, MessageEvent> context);

    void editTimeRecord(StateContext<EditReportState, MessageEvent> context);

    void requestEditAdditionalData(StateContext<EditReportState, MessageEvent> context);

    void sendCategoryButtons(StateContext<EditReportState, MessageEvent> context);

    void requestSaveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context);

    void saveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context);

    void requestEditAdditionalTimeRecord(StateContext<EditReportState, MessageEvent> context);

}
