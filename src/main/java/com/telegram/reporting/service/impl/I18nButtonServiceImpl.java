package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.dialogs.SubDialogHandlerDelegate;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.I18nKey;
import com.telegram.reporting.repository.dto.Ordinal;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.strategy.SubDialogHandlerDelegateStrategy;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class I18nButtonServiceImpl implements I18nButtonService {
    private final I18nPropsResolver i18nPropsResolver;
    private final CategoryService categoryService;
    private final List<DialogProcessor> dialogProcessors;
    private final List<DialogHandler> dialogHandlers;
    private final List<SubDialogHandlerDelegate> subDialogHandlers;
    private final SubDialogHandlerDelegateStrategy subDialogHandlerDelegateStrategy;


    @Override
    public boolean hasAvailableCategoryButtons(String timeRecordsJson) {
        List<Category> categories = categoryService.getAll(false);
        return categories.stream()
                .map(Category::getNameKey)
                .anyMatch(isCategoryNotOccupied(timeRecordsJson));
    }

    @Override
    public String getButtonLabel(Long chatId, ButtonLabelKey labelKey) {
        return i18nPropsResolver.getPropsValue(chatId, labelKey);
    }

    @Override
    public List<List<InlineKeyboardButton>> getAvailableCategoryInlineButtons(Long chatId, String timeRecordsJson, int buttonsInRow) {
        List<Category> categories = categoryService.getAll(false);
        List<InlineKeyboardButton> availableCategoryLabels = categories.stream()
                .map(Category::getNameKey)
                .filter(isCategoryNotOccupied(timeRecordsJson))
                .map(ButtonLabelKey::getByKey)
                .filter(Objects::nonNull)
                .map(key -> createInlineButton(chatId, key))
                .toList();

        if (availableCategoryLabels.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return KeyboardUtils.separateInlineButtonsToRows(availableCategoryLabels, buttonsInRow);
    }

    @Override
    public List<List<InlineKeyboardButton>> getLanguageInlineButtons(Long chatId) {
        //TODO change implementation to autogenerate all available locale buttons
        return createInlineButtonRows(chatId,
                List.of(ButtonLabelKey.GL_UA_LOCALE),
                List.of(ButtonLabelKey.GL_RU_LOCALE));
    }

    @Override
    public List<List<InlineKeyboardButton>> getRootMenuButtons(Long chatId, User user) {
        // getting all available dialog starter buttons
        Set<ButtonLabelKey> availableMenuButtons = dialogProcessors.stream()
                .map(DialogProcessor::startDialogButtonKey)
                .collect(Collectors.toSet());

        // adding all submenu button starters
        subDialogHandlers.stream()
                .map(SubDialogHandlerDelegate::getRootMenuTemplate)
                .flatMap(Collection::stream)
                .forEach(availableMenuButtons::addAll);

        // filtering by access role & sorting the root menu template
        List<List<ButtonLabelKey>> rootMenuTemplate = dialogHandlers.stream()
                .filter(handler -> CommonUtils.hasAccess(handler, user))
                .sorted(Comparator.comparing(DialogHandler::displayOrder))
                .map(DialogHandler::getRootMenuTemplate)
                .flatMap(Collection::stream)
                // required to be able to remove not available buttons from template
                .map(row -> (List<ButtonLabelKey>) new ArrayList<>(row))
                .toList();

        return convertTemplateToInlineButtons(chatId, rootMenuTemplate, availableMenuButtons);
    }

    @Override
    public List<List<InlineKeyboardButton>> getSubMenuButtons(Long chatId, DialogHandlerAlias dialogHandlerAlias) {
        // getting all available dialog starter buttons
        Set<ButtonLabelKey> availableMenuButtons = dialogProcessors.stream()
                .map(DialogProcessor::startDialogButtonKey)
                .collect(Collectors.toSet());

        // getting sub menu template
        List<List<ButtonLabelKey>> subMenuTemplate = subDialogHandlerDelegateStrategy.getDelegate(dialogHandlerAlias)
                .getSubMenuTemplate().stream()
                // required to be able to remove not available buttons from template
                .map(row -> (List<ButtonLabelKey>) new ArrayList<>(row))
                .toList();

        return convertTemplateToInlineButtons(chatId, subMenuTemplate, availableMenuButtons);
    }

    @Override
    public List<List<InlineKeyboardButton>> createInlineButtonRows(Long chatId, List<ButtonLabelKey>... rowButtons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (List<ButtonLabelKey> keys : rowButtons) {
            List<InlineKeyboardButton> buttons = keys.stream()
                    .map(key -> createInlineButton(chatId, key))
                    .toList();

            rows.add(buttons);
        }
        return rows;
    }

    @Override
    public ReplyKeyboard createSingleRowInlineMarkup(Long chatId, MenuButtons addMenu, ButtonLabelKey... rowButtons) {
        List<List<InlineKeyboardButton>> rows = createInlineButtonRows(chatId, List.of(rowButtons));
        return createMarkupWithMenuButtons(chatId, addMenu, rows);
    }

    @Override
    public ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, int buttonsInRow, ButtonLabelKey... rowButtons) {
        List<InlineKeyboardButton> labelKeys = Arrays.stream(rowButtons)
                .map(key -> createInlineButton(chatId, key))
                .toList();

        List<List<InlineKeyboardButton>> rows = KeyboardUtils.separateInlineButtonsToRows(labelKeys, buttonsInRow);

        return createMarkupWithMenuButtons(chatId, addMenu, rows);
    }

    @Override
    public ReplyKeyboard createInlineMarkup(Long chatId, MenuButtons addMenu, List<List<InlineKeyboardButton>> rows) {
        return createMarkupWithMenuButtons(chatId, addMenu, rows);
    }

    @Override
    public ReplyKeyboard createOrdinalButtonsInlineMarkup(Long chatId, MenuButtons addMenu, List<? extends Ordinal> ordinalButtons, int buttonsInRow) {
        List<InlineKeyboardButton> buttons = ordinalButtons.stream()
                .map(ordinal -> {
                    String buttonLabel = ordinal.getOrdinal().toString();
                    var inlineButton = new InlineKeyboardButton(buttonLabel);
                    inlineButton.setCallbackData(buttonLabel);
                    return inlineButton;
                })
                .toList();

        List<List<InlineKeyboardButton>> rows = KeyboardUtils.separateInlineButtonsToRows(buttons, buttonsInRow);

        return createMarkupWithMenuButtons(chatId, addMenu, rows);
    }

    @Override
    public ReplyKeyboard createMainMenuInlineMarkup(Long chatId) {
        return createSingleRowInlineMarkup(chatId, MenuButtons.NONE, ButtonLabelKey.COMMON_RETURN_MAIN_MENU);
    }

    private InlineKeyboardButton createInlineButton(Long chatId, I18nKey key) {
        var button = new InlineKeyboardButton();
        button.setText(i18nPropsResolver.getPropsValue(chatId, key));
        button.setCallbackData(key.value());
        return button;
    }

    private ReplyKeyboard createMarkupWithMenuButtons(Long chatId, MenuButtons addMenu, List<List<InlineKeyboardButton>> rows) {
        return switch (addMenu) {
            case MAIN_MENU -> new InlineKeyboardMarkup(addMainMenuButton(chatId, rows));
            case MANAGER_MENU -> new InlineKeyboardMarkup(addManagerMenuButtons(chatId, rows));
            case ADMIN_MENU -> new InlineKeyboardMarkup(addAdminMenuButtons(chatId, rows));
            case NONE -> new InlineKeyboardMarkup(rows);
        };
    }

    private List<List<InlineKeyboardButton>> addMainMenuButton(Long chatId, List<List<InlineKeyboardButton>> rows) {
        var mainMenuButtons = createInlineButtonRows(chatId, List.of(ButtonLabelKey.COMMON_RETURN_MAIN_MENU));
        rows.addAll(mainMenuButtons);
        return rows;
    }

    private List<List<InlineKeyboardButton>> addAdminMenuButtons(Long chatId, List<List<InlineKeyboardButton>> rows) {
        var adminMenuButtons = createInlineButtonRows(chatId,
                List.of(ButtonLabelKey.COMMON_RETURN_ADMIN_MENU),
                List.of(ButtonLabelKey.COMMON_RETURN_MAIN_MENU));
        rows.addAll(adminMenuButtons);
        return rows;
    }

    private List<List<InlineKeyboardButton>> addManagerMenuButtons(Long chatId, List<List<InlineKeyboardButton>> rows) {
        var managerMenuButtons = createInlineButtonRows(chatId,
                List.of(ButtonLabelKey.COMMON_RETURN_MANAGER_MENU),
                List.of(ButtonLabelKey.COMMON_RETURN_MAIN_MENU));

        rows.addAll(managerMenuButtons);
        return rows;
    }

    private Predicate<String> isCategoryNotOccupied(String timeRecordsJson) {
        if (StringUtils.isBlank(timeRecordsJson)) {
            return category -> true;
        }
        return Predicate.not(timeRecordsJson::contains);
    }

    private List<List<InlineKeyboardButton>> convertTemplateToInlineButtons(Long chatId, List<List<ButtonLabelKey>> menuTemplate, Set<ButtonLabelKey> availableMenuButtons) {
        List<List<ButtonLabelKey>> filteredSubMenu = new ArrayList<>();

        // removing not available buttons from root menu template
        for (List<ButtonLabelKey> menuRow : menuTemplate) {
            List<ButtonLabelKey> toRemove = new ArrayList<>();
            menuRow.forEach(label -> {
                if (!availableMenuButtons.contains(label)) {
                    toRemove.add(label);
                }
            });

            menuRow.removeAll(toRemove);
            filteredSubMenu.add(menuRow);
        }

        return filteredSubMenu.stream()
                .filter(Predicate.not(Collection::isEmpty))
                .map(labelKeys -> createInlineButtonRows(chatId, labelKeys))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}

