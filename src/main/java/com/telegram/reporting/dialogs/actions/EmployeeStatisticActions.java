package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.manager.employee_statistic.EmployeeStatisticState;
import org.springframework.statemachine.StateContext;

public interface EmployeeStatisticActions {
    void requestInputMonth(StateContext<EmployeeStatisticState, MessageEvent> context);

    void sendListUsers(StateContext<EmployeeStatisticState, MessageEvent> context);

    void sendListTimeRecords(StateContext<EmployeeStatisticState, MessageEvent> context);

    void requestToLockDataToEdit(StateContext<EmployeeStatisticState, MessageEvent> context);

    void sendLockReportStatusInfo(StateContext<EmployeeStatisticState, MessageEvent> context);

    void handleChoiceLockReportDataToEdit(StateContext<EmployeeStatisticState, MessageEvent> context);

    void requestReturnToListEmployees(StateContext<EmployeeStatisticState, MessageEvent> context);
}
