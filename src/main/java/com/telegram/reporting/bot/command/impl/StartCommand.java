package com.telegram.reporting.bot.command.impl;

import com.telegram.reporting.bot.command.Command;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private static final String ALIAS = "/start";

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final DialogRouterService dialogRouterService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18nMessageService;

    @Override
    public String alias() {
        return ALIAS;
    }

    @Override
    public void execute(Update update) {
        Long chatId = CommonUtils.currentChatId(update);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(i18nMessageService.getMessage(chatId, MessageKey.PD_BOT_FIRST_GREETING));

        User user = telegramUserService.findByChatId(chatId);

        if (Objects.isNull(user)) {
            log.error("Can't find user by chatId={}. TelegramUser={}", chatId, update.getMessage().getFrom());

            String buttonLabel = i18nButtonService.getButtonLabel(chatId, ButtonLabelKey.COMMON_SHARE_PHONE);
            KeyboardRow shareContact = KeyboardUtils.createSimpleButton(buttonLabel);
            shareContact.get(0).setRequestContact(true);
            sendBotMessageService.sendMessageWithKeys(message, KeyboardUtils.createKeyboardMarkup(shareContact));
        } else {
            dialogRouterService.startFlow(chatId, user.getLocale());
        }
    }
}
