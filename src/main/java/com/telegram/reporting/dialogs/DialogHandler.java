package com.telegram.reporting.dialogs;

import com.telegram.reporting.repository.entity.Role;

import java.util.List;

public interface DialogHandler {

    void handleTelegramUserInput(Long chatId, String input);

    void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey);

    void createStateMachineHandler(Long chatId, ButtonLabelKey buttonLabelKey);

    List<List<ButtonLabelKey>> getRootMenuButtons();

    void removeStateMachineHandler(Long chatId);

    boolean belongToDialogStarter(ButtonLabelKey buttonLabelKey);

    List<Role> roleAccessibility();

    Integer displayOrder();
}
