package com.telegram.reporting.dialogs.admin_dialogs.list_users;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "ListUsersDialogStateMachineFactory")
public class ListUsersDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<ListUsersState, ListUsersEvent> {
    private final ListUsersActions listUsersActions;

    public ListUsersDialogStateMachineFactory(@Lazy ListUsersActions listUsersActions) {
        this.listUsersActions = listUsersActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ListUsersState, ListUsersEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ListUsersState, ListUsersEvent> states) throws Exception {
        states.withStates()
                .initial(ListUsersState.START_LIST_USERS_DIALOG)
                .end(ListUsersState.END_LIST_USERS_DIALOG)
                .states(EnumSet.allOf(ListUsersState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ListUsersState, ListUsersEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(ListUsersState.START_LIST_USERS_DIALOG)
                .event(ListUsersEvent.RUN_LIST_USERS_DIALOG)
                .target(ListUsersState.USER_LIST_USERS_WATCH)
                .action(listUsersActions::sendListUsers)
                .action(listUsersActions::sendSelectionStatusButtons)

                .and().withExternal()
                .source(ListUsersState.USER_LIST_USERS_WATCH)
                .event(ListUsersEvent.SHOW_ACTIVE_USERS)
                .target(ListUsersState.USER_LIST_USERS_WATCH)
                .action(listUsersActions::sendListUsers)
                .action(listUsersActions::sendSelectionStatusButtons)

                .and().withExternal()
                .source(ListUsersState.USER_LIST_USERS_WATCH)
                .event(ListUsersEvent.SHOW_DELETED_USERS)
                .target(ListUsersState.USER_LIST_USERS_WATCH)
                .action(listUsersActions::sendListUsers)
                .action(listUsersActions::sendSelectionStatusButtons)

                .and().withExternal()
                .source(ListUsersState.USER_LIST_USERS_WATCH)
                .event(ListUsersEvent.SHOW_NOT_VERIFIED_USERS)
                .target(ListUsersState.USER_LIST_USERS_WATCH)
                .action(listUsersActions::sendListUsers)
                .action(listUsersActions::sendSelectionStatusButtons);
    }
}
