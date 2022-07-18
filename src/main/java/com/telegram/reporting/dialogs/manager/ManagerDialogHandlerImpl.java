package com.telegram.reporting.dialogs.manager;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.dialogs.SubDialogHandler;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("ManagerDialogHandler")
public class ManagerDialogHandlerImpl implements DialogHandler, SubDialogHandler {
    private final Map<Long, StateMachineHandler> stateMachineHandlers;
    private final StateMachineHandler employeeStatisticHandler;
    private final SendBotMessageService sendBotMessageService;


    public ManagerDialogHandlerImpl(@Qualifier("EmployeeStatisticStateMachineHandler") StateMachineHandler employeeStatisticHandler, SendBotMessageService sendBotMessageService) {
        stateMachineHandlers = new ConcurrentHashMap<>();

        this.employeeStatisticHandler = employeeStatisticHandler;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void handleTelegramInput(Long chatId, String input) {
        Optional<ButtonValue> optionalButtonValue = ButtonValue.getByText(input);

        if (optionalButtonValue.isPresent()) {
            ButtonValue buttonValue = optionalButtonValue.get();

            // return to admin menu when click 'admin menu' button
            if (ButtonValue.RETURN_MANAGER_MENU.equals(buttonValue)) {
                startSubDialogFlow(chatId);
                return;
            }
            if (!stateMachineHandlers.containsKey(chatId) && belongToSubDialogStarter(buttonValue)) {
                createStateMachineHandler(chatId, buttonValue);
            }

            if (stateMachineHandlers.containsKey(chatId)) {
                stateMachineHandlers.get(chatId).handleMessage(chatId, buttonValue);
            }
            return;
        }

        if (stateMachineHandlers.containsKey(chatId)) {
            stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
        }
    }

    @Override
    public void createStateMachineHandler(Long chatId, ButtonValue buttonValue) {
        StateMachineHandler handler = switch (buttonValue) {
            case EMPLOYEE_STATISTIC_START_DIALOG -> employeeStatisticHandler.initStateMachine(chatId);
            default -> null;
        };
        stateMachineHandlers.put(chatId, handler);
    }

    @Override
    public List<KeyboardRow> getRootMenuButtons() {
        return List.of(KeyboardUtils.createButton(ButtonValue.MANAGER_MENU_START_DIALOG.text()));
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {
        stateMachineHandlers.remove(chatId);
    }

    @Override
    public boolean belongToDialogStarter(ButtonValue buttonValue) {
        return ButtonValue.MANAGER_MENU_START_DIALOG.equals(buttonValue);
    }

    @Override
    public List<Role> roleAccessibility() {
        return List.of(Role.MANAGER_ROLE, Role.ADMIN_ROLE);
    }

    @Override
    public void startSubDialogFlow(Long chatId) {
        removeStateMachineHandler(chatId);
        final String startFlowMessage = "Выбери диалог из меню менеджера.";

        KeyboardRow[] keyboardRows = getSubMenuButtons().toArray(KeyboardRow[]::new);

        SendMessage sendMessage = new SendMessage(chatId.toString(), startFlowMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, keyboardRows));
    }

    @Override
    public List<KeyboardRow> getSubMenuButtons() {
        return List.of(
                KeyboardUtils.createButton(ButtonValue.EMPLOYEE_STATISTIC_START_DIALOG.text()));
    }

    @Override
    public boolean belongToSubDialogStarter(ButtonValue buttonValue) {
        return getSubMenuButtons().stream()
                .flatMap(KeyboardRow::stream)
                .map(KeyboardButton::getText)
                .anyMatch(buttonName -> buttonName.equals(buttonValue.text()));
    }
}
