package com.telegram.reporting.dialogs;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface SubDialogHandler {

    void startSubDialogFlow(Long chatId);

    List<List<InlineKeyboardButton>> getSubMenuButtons(Long chatId);

    default boolean belongToSubDialogStarter(Long chatId, ButtonLabelKey buttonLabelKey) {
        return getSubMenuButtons(chatId).stream()
                .flatMap(Collection::stream)
                .map(InlineKeyboardButton::getCallbackData)
                .anyMatch(Predicate.isEqual(buttonLabelKey.value()));
    }
}
