package com.telegram.reporting.bot.event;

import com.telegram.reporting.i18n.ButtonLabelKey;

public record InlineButtonEvent(Long chatId, ButtonLabelKey buttonLabelKey) implements TelegramEvent {
}
