package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.edit_dialog.EditReportState;
import org.springframework.statemachine.StateContext;

public interface EditReportActionService {

    void requestChooseEditData(StateContext<EditReportState, MessageEvent> context);

    void sendDataToEdit(StateContext<EditReportState, MessageEvent> context);

    void editTimeRecord(StateContext<EditReportState, MessageEvent> context);

    void requestEditAdditionalData(StateContext<EditReportState, MessageEvent> context);

    void sendCategoryButtons(StateContext<EditReportState, MessageEvent> context);

    void requestSaveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context);

    void saveTimeRecordChanges(StateContext<EditReportState, MessageEvent> context);

    void requestEditAdditionalTimeRecord(StateContext<EditReportState, MessageEvent> context);

}
