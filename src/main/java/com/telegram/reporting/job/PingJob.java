package com.telegram.reporting.job;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Job for finding new posts.
 */
@Slf4j
@Component
public class PingJob {

    public static final String pingMessage = "Ping!";

    private final TelegramUserService telegramUserService;
    private final SendBotMessageService sendBotMessageService;

    @Autowired
    public PingJob(TelegramUserService telegramUserService, SendBotMessageService sendBotMessageService) {
        this.telegramUserService = telegramUserService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Scheduled(fixedRateString = "${bot.updateDataFixedRate}")
    public void ping() {
        LocalDateTime start = LocalDateTime.now();
        log.info("Ping job started.");

        telegramUserService.findAll()
                .forEach(telegramUser -> sendBotMessageService.sendMessage(telegramUser.getChatId(), pingMessage));

        LocalDateTime end = LocalDateTime.now();
        log.info("Pinging finished. Took seconds: {}",
                end.toEpochSecond(ZoneOffset.UTC) - start.toEpochSecond(ZoneOffset.UTC));
    }
}
