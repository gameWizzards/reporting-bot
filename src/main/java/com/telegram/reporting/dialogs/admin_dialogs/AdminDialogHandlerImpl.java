package com.telegram.reporting.dialogs.admin_dialogs;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.dialogs.RootMenuStructure;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.SubDialogHandler;
import com.telegram.reporting.dialogs.SubMenuStructure;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.impl.MenuButtons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AdminDialogHandlerImpl implements DialogHandler, SubDialogHandler {
    private final Map<Long, StateMachineHandler> stateMachineHandlers = new ConcurrentHashMap<>();
    private final StateMachineHandler listUsersHandler;
    private final SendBotMessageService sendBotMessageService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;


    public AdminDialogHandlerImpl(@Qualifier("ListUsersStateMachineHandler") StateMachineHandler listUsersHandler,
                                  SendBotMessageService sendBotMessageService, I18nButtonService i18nButtonService,
                                  I18nMessageService i18NMessageService) {
        this.listUsersHandler = listUsersHandler;
        this.sendBotMessageService = sendBotMessageService;
        this.i18nButtonService = i18nButtonService;
        this.i18NMessageService = i18NMessageService;
    }

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {

        // return to admin submenu when click 'admin menu' button
        if (ButtonLabelKey.COMMON_RETURN_ADMIN_MENU.equals(buttonLabelKey)) {
            startSubDialogFlow(chatId);
            return;
        }

        if (!stateMachineHandlers.containsKey(chatId) && belongToSubDialogStarter(chatId, buttonLabelKey)) {
            createStateMachineHandler(chatId, buttonLabelKey);
        }

        if (stateMachineHandlers.containsKey(chatId)) {
            stateMachineHandlers.get(chatId).handleButtonClick(chatId, buttonLabelKey);
        }
    }

    @Override
    public void handleTelegramUserInput(Long chatId, String input) {
        if (stateMachineHandlers.containsKey(chatId)) {
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    @Override
    public void createStateMachineHandler(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachineHandler handler = switch (buttonLabelKey) {
            case ALU_START_DIALOG -> listUsersHandler.initStateMachine(chatId);
            default ->
                    throw new IllegalArgumentException("Can't find mapping of button to stateMachine handler. Button=" + buttonLabelKey);
        };
        stateMachineHandlers.put(chatId, handler);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuButtons() {
        return RootMenuStructure.ADMIN_ROOT_MENU_STRUCTURE;
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {
        if (Objects.nonNull(stateMachineHandlers.get(chatId))) {
            stateMachineHandlers.remove(chatId).removeDialogData(chatId);
        }
    }

    @Override
    public boolean belongToDialogStarter(ButtonLabelKey buttonLabelKey) {
        return RootMenuStructure.ADMIN_ROOT_MENU_STRUCTURE.stream()
                .flatMap(Collection::stream)
                .anyMatch(buttonLabelKey::equals);
    }

    @Override
    public List<Role> roleAccessibility() {
        return List.of(Role.ADMIN_ROLE);
    }

    @Override
    public Integer displayOrder() {
        return 3;
    }

    @Override
    public void startSubDialogFlow(Long chatId) {
        removeStateMachineHandler(chatId);
        String startFlowMessage = i18NMessageService.getMessage(chatId, MessageKey.ASD_START_SUB_DIALOG_FLOW);

        List<List<InlineKeyboardButton>> subMenuDialogButtons = getSubMenuButtons(chatId);

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, subMenuDialogButtons);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), startFlowMessage), inlineMarkup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<List<InlineKeyboardButton>> getSubMenuButtons(Long chatId) {
        return SubMenuStructure.ADMIN_SUB_MENU_STRUCTURE.stream()
                .map(labelKeys -> i18nButtonService.createInlineButtonRows(chatId, labelKeys))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
