package com.telegram.reporting.service;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.repository.dto.Ordinal;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface I18nButtonService {
    boolean hasAvailableCategoryButtons(String timeRecordsJson);

    String getButtonLabel(Long chatId, ButtonLabelKey labelKey);

    List<List<InlineKeyboardButton>> getAvailableCategoryInlineButtons(Long chatId, String timeRecordsJson, int buttonsInRow);

    List<List<InlineKeyboardButton>> getLanguageInlineButtons(Long chatId);

    @SuppressWarnings("unchecked")
    List<List<InlineKeyboardButton>> createInlineButtonRows(Long chatId, List<ButtonLabelKey>... rowButtons);

    ReplyKeyboard createSingleRowInlineMarkup(Long chatId, MenuButtons addMenu, ButtonLabelKey... rowButtons);

    ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, int buttonsInRow, ButtonLabelKey... rowButtons);

    ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, List<List<InlineKeyboardButton>> rows);

    ReplyKeyboard createOrdinalButtonsInlineMarkup(Long chatId, MenuButtons addMenu, List<? extends Ordinal> ordinalButtons, int buttonsInRow);

    ReplyKeyboard createMainMenuInlineMarkup(Long chatId);

}