package com.telegram.reporting.dialogs;

import com.telegram.reporting.repository.entity.Role;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public interface DialogHandler {

    void handleTelegramInput(Long chatId, String input);

    void createStateMachineHandler(Long chatId, ButtonValue buttonValue);

    List<KeyboardRow> getRootMenuButtons();

    void removeStateMachineHandler(Long chatId);

    boolean belongToDialogStarter(ButtonValue buttonValue);

    List<Role> roleAccessibility();
}
