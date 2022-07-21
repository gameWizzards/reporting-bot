package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.manager.add_employee.AddEmployeeState;
import org.springframework.statemachine.StateContext;

public interface AddEmployeeActions {
    void requestInputEmployeePhone(StateContext<AddEmployeeState, MessageEvent> context);

    void saveNewEmployeePhone(StateContext<AddEmployeeState, MessageEvent> context);
}