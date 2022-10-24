package com.telegram.reporting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/ping")
public class HealthCheckController {

    @GetMapping
    public String ping() {
        return "Server time is: %s".formatted(LocalDateTime.now());
    }

}