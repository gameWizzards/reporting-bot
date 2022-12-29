package com.telegram.reporting.dialogs.general_dialogs.language;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dialogs.DefaultDialogListener;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.exception.ButtonToEventMappingException;
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
@RequiredArgsConstructor
@Component("LanguageStateMachineHandler")
public class LanguageStateMachineHandler implements StateMachineHandler {

    private final Map<Long, StateMachine<LanguageState, LanguageEvent>> stateMachines = new ConcurrentHashMap<>();
    private final StateMachineFactory<LanguageState, LanguageEvent> stateMachineFactory;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18NMessageService;


    @Override
    public void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachine<LanguageState, LanguageEvent> stateMachine = stateMachines.get(chatId);
        LanguageEvent messageEvent = switch (buttonLabelKey) {
            case GL_START_DIALOG -> LanguageEvent.RUN_LANGUAGE_DIALOG;
            case GL_UA_LOCALE, GL_RU_LOCALE -> LanguageEvent.HANDLE_USER_CHANGE_LOCALE;
            default ->
                    throw new ButtonToEventMappingException(chatId, "[Language] Can't find mapping of button to Message event handler. Button=" + buttonLabelKey);
        };
        stateMachine.getExtendedState()
                .getVariables()
                .put(ContextVarKey.BUTTON_CALLBACK_VALUE, buttonLabelKey.value());

        stateMachine.sendEvent(Mono.just(new GenericMessage<>(messageEvent)))
                .subscribe();
    }

    @Override
    public void handleUserInput(Long chatId, String userInput) {
        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_USER_INPUT_INSTEAD_BTN));
    }

    @Override
    public StateMachineHandler initStateMachine(Long chatId) {
        StateMachine<LanguageState, LanguageEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.CHAT_ID, chatId);
        stateMachine.getExtendedState().getVariables().put(ContextVarKey.LOG_PREFIX, CommonUtils.createLogPrefix("Language", chatId));
        stateMachine.addStateListener(new DefaultDialogListener<>(stateMachine));
        stateMachines.put(chatId, stateMachine);
        return this;
    }

    @Override
    public void removeDialogData(Long chatId) {
        stateMachines.get(chatId).getExtendedState().getVariables().clear();
    }
}

