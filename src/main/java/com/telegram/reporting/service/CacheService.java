package com.telegram.reporting.service;

import java.time.LocalDate;

public interface CacheService {
    // Key format String = chatId+month+year
    String EMPLOYEE_STATISTIC_CACHE = "employeeStatistic";
    String MANAGER_STATISTIC_CACHE = "managerStatistic";

    void evictCache(String cacheName, String key);

    void evictCache(String cacheName, Long chatId, LocalDate dateKeyPart);
}
