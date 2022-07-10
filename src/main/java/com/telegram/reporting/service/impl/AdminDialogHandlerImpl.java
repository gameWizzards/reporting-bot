package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.service.DialogHandler;
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
    public void handleTelegramInput(String chatId, String input) {

    }

    @Override
    public void createStateMachineHandler(String chatId, ButtonValue buttonValue) {

    }

    @Override
    public List<KeyboardRow> getRootMenuButtons(String chatId) {
        return List.of(KeyboardUtils.createButton(ButtonValue.ADMIN_MENU.text()));
    }

    @Override
    public void removeStateMachineHandler(String chatId) {

    }

    @Override
    public boolean belongToDialogStarter(ButtonValue buttonValue) {
        return ButtonValue.ADMIN_MENU.equals(buttonValue);
    }

    @Override
    public boolean belongToSubDialogStarter(ButtonValue buttonValue) {
        return false;
    }
}
