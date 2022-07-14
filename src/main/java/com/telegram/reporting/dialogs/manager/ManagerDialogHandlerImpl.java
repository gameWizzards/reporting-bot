package com.telegram.reporting.dialogs.manager;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.dialogs.SubDialogHandler;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Component("ManagerDialogHandler")
public class ManagerDialogHandlerImpl implements DialogHandler, SubDialogHandler {

    @Override
    public void createStateMachineHandler(Long chatId, ButtonValue buttonValue) {
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {

    }

    @Override
    public void handleTelegramInput(Long chatId, String input) {

    }

    @Override
    public List<KeyboardRow> getRootMenuButtons() {
        return List.of(KeyboardUtils.createButton(ButtonValue.MANAGER_MENU_START_DIALOG.text()));
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

    }

    @Override
    public List<KeyboardRow> getSubMenuButtons() {
        return null;
    }

    @Override
    public boolean belongToSubDialogStarter(ButtonValue buttonValue) {
        return false;
    }
}
