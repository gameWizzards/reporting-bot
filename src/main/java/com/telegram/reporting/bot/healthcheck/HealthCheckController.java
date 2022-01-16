package com.telegram.reporting.bot.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HealthCheckController {

    @GetMapping("/ping")
    public String ping() {
        return "Server time is: " + LocalDateTime.now();
    }

}