package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.manager.employee_status.EmployeeStatusState;
import org.springframework.statemachine.StateContext;

public interface EmployeeStatusActions {
    void requestListEmployeesChoose(StateContext<EmployeeStatusState, MessageEvent> context);

    void sendListUsers(StateContext<EmployeeStatusState, MessageEvent> context);

    void requestReturnToListEmployees(StateContext<EmployeeStatusState, MessageEvent> context);

    void sendEmployeeInfo(StateContext<EmployeeStatusState, MessageEvent> context);

    void sendEditStatusInfo(StateContext<EmployeeStatusState, MessageEvent> context);

    void handleEmployeeEditStatus(StateContext<EmployeeStatusState, MessageEvent> context);

    void sendEditRoleInfo(StateContext<EmployeeStatusState, MessageEvent> context);

    void handleEmployeeEditRole(StateContext<EmployeeStatusState, MessageEvent> context);

    void requestChangeAdditionalData(StateContext<EmployeeStatusState, MessageEvent> context);
}
