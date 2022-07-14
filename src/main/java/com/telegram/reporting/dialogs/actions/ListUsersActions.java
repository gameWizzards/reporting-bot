package com.telegram.reporting.dialogs.actions;

import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.admin.list_users.ListUsersState;
import org.springframework.statemachine.StateContext;

public interface ListUsersActions {

    void sendListUsers(StateContext<ListUsersState, MessageEvent> context);

    void sendSelectionStatusButtons(StateContext<ListUsersState, MessageEvent> context);
}
