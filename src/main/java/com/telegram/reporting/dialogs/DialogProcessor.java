package com.telegram.reporting.dialogs;

import com.telegram.reporting.i18n.ButtonLabelKey;

public interface DialogProcessor {
    void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey);

    void handleUserInput(Long chatId, String userInput);

    DialogProcessor initDialogProcessor(Long chatId);

    void removeDialogData(Long chatId);

    ButtonLabelKey startDialogButtonKey();
}
