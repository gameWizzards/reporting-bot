package com.telegram.reporting.bot.command.impl;

import com.telegram.reporting.bot.event.CommandEvent;
import com.telegram.reporting.bot.command.Command;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public void execute(CommandEvent event) {
        sendBotMessageService.sendMessage(
                event.chatId(),
                i18NMessageService.getMessage(event.chatId(), MessageKey.PD_UNKNOWN_COMMAND_CHOSEN));
    }
}
