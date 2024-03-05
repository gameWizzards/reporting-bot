package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dto.Ordinal;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.impl.MenuButtons;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface I18nButtonService {
    boolean hasAvailableCategoryButtons(String timeRecordsJson);

    String getButtonLabel(Long chatId, ButtonLabelKey labelKey);

    List<List<InlineKeyboardButton>> getUnoccupiedCategoryInlineButtons(Long chatId, String timeRecordsJson, int buttonsInRow);

    List<List<InlineKeyboardButton>> getLanguageInlineButtons(Long chatId);

    List<List<InlineKeyboardButton>> getRootMenuButtons(Long chatId, User user);

    List<List<InlineKeyboardButton>> getSubMenuButtons(Long chatId, DialogHandlerAlias dialogHandlerAlias);

    @SuppressWarnings("unchecked")
    List<List<InlineKeyboardButton>> createInlineButtonRows(Long chatId, List<ButtonLabelKey>... rowButtons);

    ReplyKeyboard createSingleRowInlineMarkup(Long chatId, MenuButtons addMenu, ButtonLabelKey... rowButtons);

    ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, int buttonsInRow, ButtonLabelKey... rowButtons);

    ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, List<List<InlineKeyboardButton>> rows);

    ReplyKeyboard createOrdinalButtonsInlineMarkup(Long chatId, MenuButtons addMenu, List<? extends Ordinal> ordinalButtons, int buttonsInRow);

    ReplyKeyboard createMainMenuInlineMarkup(Long chatId);
}