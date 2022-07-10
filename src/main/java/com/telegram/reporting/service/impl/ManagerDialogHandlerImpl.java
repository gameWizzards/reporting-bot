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
@Component("ManagerDialogHandler")
public class ManagerDialogHandlerImpl implements DialogHandler, SubDialogHandler {

    @Override
    public void createStateMachineHandler(String chatId, ButtonValue buttonValue) {
    }

    @Override
    public void removeStateMachineHandler(String chatId) {

    }

    @Override
    public void handleTelegramInput(String chatId, String input) {

    }

    @Override
    public List<KeyboardRow> getRootMenuButtons(String chatId) {
        return List.of(KeyboardUtils.createButton(ButtonValue.MANAGER_MENU.text()));
    }

    @Override
    public boolean belongToDialogStarter(ButtonValue buttonValue) {
        return ButtonValue.MANAGER_MENU.equals(buttonValue);
    }

    @Override
    public boolean belongToSubDialogStarter(ButtonValue buttonValue) {
        return false;
    }
}
