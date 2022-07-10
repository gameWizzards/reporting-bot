package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.ButtonValue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public interface DialogHandler {

    void handleTelegramInput(String chatId, String input);

    void createStateMachineHandler(String chatId, ButtonValue buttonValue);

    List<KeyboardRow> getRootMenuButtons(String chatId);

    void removeStateMachineHandler(String chatId);

    boolean belongToDialogStarter(ButtonValue buttonValue);
}
