package com.telegram.reporting.dialogs.manager_dialogs;

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
@Component("ManagerDialogHandler")
public class ManagerDialogHandlerImpl implements DialogHandler, SubDialogHandler {
    private final Map<Long, StateMachineHandler> stateMachineHandlers;
    private final StateMachineHandler employeeStatisticHandler;
    private final StateMachineHandler addEmployeeHandler;
    private final StateMachineHandler employeeStatusHandler;
    private final SendBotMessageService sendBotMessageService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService I18nMessageService;


    public ManagerDialogHandlerImpl(@Qualifier("EmployeeStatisticStateMachineHandler") StateMachineHandler employeeStatisticHandler,
                                    @Qualifier("AddEmployeeStateMachineHandler") StateMachineHandler addEmployeeHandler,
                                    @Qualifier("EmployeeStatusStateMachineHandler") StateMachineHandler employeeStatusHandler,
                                    SendBotMessageService sendBotMessageService,
                                    I18nButtonService i18nButtonService, I18nMessageService I18nMessageService) {
        stateMachineHandlers = new ConcurrentHashMap<>();

        this.employeeStatisticHandler = employeeStatisticHandler;
        this.addEmployeeHandler = addEmployeeHandler;
        this.employeeStatusHandler = employeeStatusHandler;

        this.sendBotMessageService = sendBotMessageService;

        this.i18nButtonService = i18nButtonService;
        this.I18nMessageService = I18nMessageService;
    }

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {

        // return to admin menu when click 'admin menu' button
        if (ButtonLabelKey.COMMON_RETURN_MANAGER_MENU.equals(buttonLabelKey)) {
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
            case MES_START_DIALOG -> employeeStatisticHandler.initStateMachine(chatId);
            case MAE_START_DIALOG -> addEmployeeHandler.initStateMachine(chatId);
            case MESTATUS_START_DIALOG -> employeeStatusHandler.initStateMachine(chatId);
            default ->
                    throw new IllegalArgumentException("[Manager dialogs] Can't find mapping of button to stateMachine handler. Button=" + buttonLabelKey);
        };
        stateMachineHandlers.put(chatId, handler);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuButtons() {
        return RootMenuStructure.MANAGER_ROOT_MENU_STRUCTURE;
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {
        if (Objects.nonNull(stateMachineHandlers.get(chatId))) {
            stateMachineHandlers.remove(chatId).removeDialogData(chatId);
        }
    }

    @Override
    public boolean belongToDialogStarter(ButtonLabelKey buttonLabelKey) {
        return RootMenuStructure.MANAGER_ROOT_MENU_STRUCTURE.stream()
                .flatMap(Collection::stream)
                .anyMatch(buttonLabelKey::equals);
    }

    @Override
    public List<Role> roleAccessibility() {
        return List.of(Role.MANAGER_ROLE, Role.ADMIN_ROLE);
    }

    @Override
    public Integer displayOrder() {
        return 2;
    }

    @Override
    public void startSubDialogFlow(Long chatId) {
        removeStateMachineHandler(chatId);
        final String startFlowMessage = I18nMessageService.getMessage(chatId, MessageKey.MSD_START_SUB_DIALOG_FLOW);

        List<List<InlineKeyboardButton>> subMenuDialogButtons = getSubMenuButtons(chatId);

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, subMenuDialogButtons);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), startFlowMessage), inlineMarkup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<List<InlineKeyboardButton>> getSubMenuButtons(Long chatId) {
        return SubMenuStructure.MANAGER_SUB_MENU_STRUCTURE.stream()
                .map(labelKeys -> i18nButtonService.createInlineButtonRows(chatId, labelKeys))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
