package com.telegram.reporting.dialogs;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.repository.entity.Role;

import java.util.Collection;
import java.util.List;

public interface DialogHandler {

    void handleTelegramUserInput(Long chatId, String input);

    void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey);

    void createDialogProcessor(Long chatId, ButtonLabelKey buttonLabelKey);

    List<List<ButtonLabelKey>> getRootMenuButtons();

    default boolean belongToRootMenuButtons(ButtonLabelKey buttonLabelKey) {
        return getRootMenuButtons().stream()
                .flatMap(Collection::stream)
                .anyMatch(buttonLabelKey::equals);
    }

    List<Role> roleAccessibility();

    void removeDialogProcessor(Long chatId);

    Integer displayOrder();

    DialogHandlerAlias dialogHandlerAlias();
}
