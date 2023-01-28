package com.telegram.reporting.dialogs;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.MenuTemplateService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.strategy.DialogProcessorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSubDialogHandlerDelegate implements SubDialogHandlerDelegate {
    private final Map<Long, DialogProcessor> activeProcesses = new ConcurrentHashMap<>();

    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18nMessageService;
    private final I18nButtonService i18nButtonService;
    private final DialogProcessorStrategy processorStrategy;
    private final MenuTemplateService menuTemplateService;

    @Override
    public void handleTelegramUserInput(Long chatId, String input) {
        activeProcesses.get(chatId).handleUserInput(chatId, input);
    }

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {
        activeProcesses.get(chatId).handleButtonClick(chatId, buttonLabelKey);
    }

    @Override
    public void createDialogProcessor(Long chatId, ButtonLabelKey buttonLabelKey) {
        DialogProcessor processor = processorStrategy.getProcessor(buttonLabelKey).initDialogProcessor(chatId);
        activeProcesses.put(chatId, processor);
    }

    @Override
    public void removeDialogProcessor(Long chatId) {
        if (Objects.nonNull(activeProcesses.get(chatId))) {
            activeProcesses.remove(chatId).removeDialogData(chatId);
        }
    }

    @Override
    public boolean isProcessorCreated(Long chatId) {
        return activeProcesses.containsKey(chatId);
    }

    @Override
    public void startSubDialogFlow(Long chatId) {
        removeDialogProcessor(chatId);

        String startFlowMessage = i18nMessageService.getMessage(chatId, getStartFlowMessageKey());

        List<List<InlineKeyboardButton>> subMenuDialogButtons = i18nButtonService.getSubMenuButtons(chatId, dialogHandlerAlias());

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, subMenuDialogButtons);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), startFlowMessage), inlineMarkup);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuTemplate() {
        return menuTemplateService.rootMenuTemplate(dialogHandlerAlias());
    }

    @Override
    public List<List<ButtonLabelKey>> getSubMenuTemplate() {
        return menuTemplateService.subMenuTemplate(dialogHandlerAlias());
    }

    public abstract DialogHandlerAlias dialogHandlerAlias();

    @Override
    public abstract MessageKey getStartFlowMessageKey();

}
