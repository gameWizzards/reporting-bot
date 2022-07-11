package com.telegram.reporting.dialogs.admin;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.service.SubDialogHandler;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Component("AdminDialogHandler")
public class AdminDialogHandlerImpl implements DialogHandler, SubDialogHandler {

    @Override
    public void handleTelegramInput(Long chatId, String input) {

    }

    @Override
    public void createStateMachineHandler(Long chatId, ButtonValue buttonValue) {

    }

    @Override
    public List<KeyboardRow> getRootMenuButtons() {
        return List.of(KeyboardUtils.createButton(ButtonValue.ADMIN_MENU.text()));
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {

    }

    @Override
    public boolean belongToDialogStarter(ButtonValue buttonValue) {
        return ButtonValue.ADMIN_MENU.equals(buttonValue);
    }

    @Override
    public List<Role> roleAccessibility() {
        return List.of(Role.ADMIN_ROLE);
    }

    @Override
    public boolean belongToSubDialogStarter(ButtonValue buttonValue) {
        return false;
    }
}
