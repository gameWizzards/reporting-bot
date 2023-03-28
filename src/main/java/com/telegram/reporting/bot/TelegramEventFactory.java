package com.telegram.reporting.bot;

import com.telegram.reporting.bot.event.CommandEvent;
import com.telegram.reporting.bot.event.DynamicOrdinalInlineButtonEvent;
import com.telegram.reporting.bot.event.InlineButtonEvent;
import com.telegram.reporting.bot.event.SendContactEvent;
import com.telegram.reporting.bot.event.TelegramEvent;
import com.telegram.reporting.bot.event.UserInputEvent;
import com.telegram.reporting.exception.TelegramEventException;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Component
public class TelegramEventFactory {

    public TelegramEvent getEvent(Update update) {

        if (CommonUtils.isTelegramCommand(update)) {
            return createCommandEvent(update);

        } else if (CommonUtils.isSendContact(update)) {
            return createSendContactEvent(update);

        } else if (CommonUtils.isInlineButton(update)) {
            if (CommonUtils.isDynamicOrdinalInlineButton(update)) {
                return createDynamicOrdinalInlineButtonEvent(update);
            }
            return createInlineButtonEvent(update);
        } else if (CommonUtils.isUserInput(update)) {
            return new UserInputEvent(CommonUtils.currentChatId(update), CommonUtils.getMessageText(update));
        }

        throw new TelegramEventException(
                CommonUtils.currentChatId(update),
                "Can't find appropriate mapping to Telegram update event. %s".formatted(update));

    }

    public CommandEvent createCommandEvent(Update update) {
        User fromUser = update.getMessage().getFrom();
        return new CommandEvent(
                CommonUtils.currentChatId(update),
                CommonUtils.getMessageText(update),
                new CommandEvent.FromUser(
                        fromUser.getFirstName(),
                        fromUser.getLastName(),
                        fromUser.getUserName(),
                        fromUser.getIsBot()));
    }

    private SendContactEvent createSendContactEvent(Update update) {
        Contact contact = update.getMessage().getContact();
        String normalizePhoneNumber = CommonUtils.normalizePhoneNumber(contact.getPhoneNumber());
        String surname = StringUtils.isNotBlank(contact.getLastName()) ? contact.getLastName() : normalizePhoneNumber;
        return new SendContactEvent(
                CommonUtils.currentChatId(update),
                normalizePhoneNumber,
                contact.getFirstName(),
                surname,
                update.getMessage().getFrom().getUserName());
    }

    private InlineButtonEvent createInlineButtonEvent(Update update) {
        String buttonCallbackData = CommonUtils.getButtonCallbackData(update);
        ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(buttonCallbackData);
        return new InlineButtonEvent(CommonUtils.currentChatId(update), buttonLabelKey);
    }

    private DynamicOrdinalInlineButtonEvent createDynamicOrdinalInlineButtonEvent(Update update) {
        String buttonCallbackData = CommonUtils.getButtonCallbackData(update);
        return new DynamicOrdinalInlineButtonEvent(CommonUtils.currentChatId(update), buttonCallbackData);
    }
}
