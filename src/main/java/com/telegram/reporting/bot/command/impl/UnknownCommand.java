package com.telegram.reporting.bot.command.impl;

import com.telegram.reporting.bot.command.Command;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component("UnknownCommand")
@RequiredArgsConstructor
public class UnknownCommand implements Command {
    private static final String ALIAS = "/unknownCommand";

    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18NMessageService;

    @Override
    public String alias() {
        return ALIAS;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(
                CommonUtils.currentChatId(update),
                i18NMessageService.getMessage(CommonUtils.currentChatId(update), MessageKey.PD_UNKNOWN_COMMAND_CHOSEN));
    }
}
