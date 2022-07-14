package com.telegram.reporting.dialogs;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public interface SubDialogHandler {

    void startSubDialogFlow(Long chatId);

    List<KeyboardRow> getSubMenuButtons();

    boolean belongToSubDialogStarter(ButtonValue buttonValue);
}
