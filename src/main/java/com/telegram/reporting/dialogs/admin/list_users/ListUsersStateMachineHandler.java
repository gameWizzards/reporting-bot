package com.telegram.reporting.dialogs.admin.list_users;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("ListUsersStateMachineHandler")
public class ListUsersStateMachineHandler implements StateMachineHandler {

    private final StateMachineFactory<ListUsersState, MessageEvent> stateMachineFactory;
    private final Map<Long, StateMachine<ListUsersState, MessageEvent>> stateMachines;
    private final SendBotMessageService sendBotMessageService;


    public ListUsersStateMachineHandler(StateMachineFactory<ListUsersState, MessageEvent> stateMachineFactory,
                                        SendBotMessageService sendBotMessageService) {
        this.stateMachineFactory = stateMachineFactory;
        this.sendBotMessageService = sendBotMessageService;
        stateMachines = new HashMap<>();
    }

    @Override
    public void handleMessage(Long chatId, ButtonValue buttonValue) {
        StateMachine<ListUsersState, MessageEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        MessageEvent messageEvent = switch (buttonValue) {
            case LIST_USERS_START_DIALOG ->  MessageEvent.RUN_LIST_USERS_DIALOG;
            case USER_STATUS_ACTIVE ->  {
                variables.put(ContextVariable.USER_STATUS, UserFilter.UserStatus.ACTIVE);
                yield MessageEvent.SHOW_ACTIVE_USERS;
            }
            case USER_STATUS_NOT_VERIFIED ->  {
                variables.put(ContextVariable.USER_STATUS, UserFilter.UserStatus.ACTIVE_NOT_VERIFIED);
                yield MessageEvent.SHOW_NOT_VERIFIED_USERS;
            }
            case USER_STATUS_DELETED ->  {
                variables.put(ContextVariable.USER_STATUS, UserFilter.UserStatus.DELETED);
                yield MessageEvent.SHOW_DELETED_USERS;
            }
            default -> null;
        };
        variables.put(ContextVariable.BUTTON_VALUE, buttonValue.text());
        Optional.ofNullable(messageEvent)
                .ifPresent(stateMachine::sendEvent);
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
       sendBotMessageService.sendMessage(chatId, "Используй кнопки. Не напрягайся печатать буквы...");
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<ListUsersState, MessageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVariable.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVariable.LOG_PREFIX, TelegramUtils.createLogPrefix("ListUsers", chatId));
        stateMachines.put(chatId, stateMachine);
        return this;
    }
}

