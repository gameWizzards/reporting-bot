package com.telegram.reporting.dialogs.admin_dialogs.list_users;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DefaultDialogListener;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.exception.ButtonToEventMappingException;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListUsersDialogProcessor implements DialogProcessor {

    private final Map<Long, StateMachine<ListUsersState, ListUsersEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<ListUsersState, ListUsersEvent> stateMachineFactory;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18NMessageService;

    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<ListUsersState, ListUsersEvent> stateMachine = stateMachines.get(chatId);
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        ListUsersEvent messageEvent = switch (buttonLabelKey) {
            case ALU_START_DIALOG -> ListUsersEvent.RUN_LIST_USERS_DIALOG;
            case ALU_USER_STATUS_ACTIVE -> {
                variables.put(ContextVarKey.USER_STATUS, UserFilter.UserStatus.ACTIVE);
                yield ListUsersEvent.SHOW_ACTIVE_USERS;
            }
            case ALU_USER_STATUS_NOT_VERIFIED -> {
                variables.put(ContextVarKey.USER_STATUS, UserFilter.UserStatus.ACTIVE_NOT_VERIFIED);
                yield ListUsersEvent.SHOW_NOT_VERIFIED_USERS;
            }
            case ALU_USER_STATUS_DELETED -> {
                variables.put(ContextVarKey.USER_STATUS, UserFilter.UserStatus.DELETED);
                yield ListUsersEvent.SHOW_DELETED_USERS;
            }
            default ->
                    throw new ButtonToEventMappingException(chatId, "[ListUsers] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        variables.put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        sendBotMessageService.sendMessage(
                chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_USER_INPUT_INSTEAD_BTN));
    }

    @Override
    public DialogProcessor initDialogProcessor(Long chatId) {
        StateMachine<ListUsersState, ListUsersEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("ListUsers", chatId));
        stateMachine.addStateListener(new DefaultDialogListener<>(stateMachine));
        stateMachines.put(chatId, stateMachine);
        return this;
    }

    @Override
    public void removeDialogData(Long chatId) {
        stateMachines.get(chatId).getExtendedState().getVariables().clear();
    }

    @Override
    public ButtonLabelKey startDialogButtonKey() {
        return ButtonLabelKey.ALU_START_DIALOG;
    }
}

