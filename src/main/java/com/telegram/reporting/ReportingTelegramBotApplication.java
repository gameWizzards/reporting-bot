package com.telegram.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReportingTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingTelegramBotApplication.class, args);
    }

}
