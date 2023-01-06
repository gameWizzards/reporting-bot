package com.telegram.reporting.dialogs;

import com.telegram.reporting.i18n.ButtonLabelKey;

import java.util.Collection;
import java.util.List;

public interface SubDialogHandlerDelegate {

    void handleTelegramUserInput(Long chatId, String input);

    void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey);

    void createDialogProcessor(Long chatId, ButtonLabelKey buttonLabelKey);

    void removeDialogProcessor(Long chatId);

    boolean isProcessorCreated(Long chatId);

    void startSubDialogFlow(Long chatId);

    List<List<ButtonLabelKey>> getSubMenuButtons();

    default boolean belongToSubMenuButtons(ButtonLabelKey buttonLabelKey) {
        return getSubMenuButtons().stream()
                .flatMap(Collection::stream)
                .anyMatch(buttonLabelKey::equals);
    }

    DialogHandlerAlias dialogHandlerAlias();
}
