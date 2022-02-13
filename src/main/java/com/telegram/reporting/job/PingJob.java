package com.telegram.reporting.job;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

//    @Scheduled(fixedRateString = "${bot.updateDataFixedRate}")
    @Scheduled(fixedDelay = 100_000L)
    public void ping() {
        long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        log.info("Ping job started.");

        telegramUserService.findAll()
                .forEach(telegramUser -> sendBotMessageService.sendMessage(telegramUser.getChatId(), pingMessage));

        long end = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        log.info("Pinging finished. Took seconds: {}", end - start);
    }
}
