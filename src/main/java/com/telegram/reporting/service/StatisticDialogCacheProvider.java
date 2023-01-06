package com.telegram.reporting.service;

import java.time.LocalDate;

public interface StatisticDialogCacheProvider {

    String getStatisticMessage(Long chatId, LocalDate statisticDate);

    void put(Long chatId, LocalDate statisticDate, String statisticMessage);

    boolean hasCachedData(Long chatId, LocalDate statisticDate);

    void evictCachedData(Long chatId);

}
