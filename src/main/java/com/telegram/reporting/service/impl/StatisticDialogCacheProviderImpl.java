package com.telegram.reporting.service.impl;

import com.telegram.reporting.service.StatisticDialogCacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StatisticDialogCacheProviderImpl implements StatisticDialogCacheProvider {
    private final Map<Long, Map<LocalDate, String>> statisticMessagesCache = new ConcurrentHashMap<>();

    public String getStatisticMessage(Long chatId, LocalDate statisticDate) {
        return statisticMessagesCache.get(chatId).get(statisticDate);
    }

    public void put(Long chatId, LocalDate statisticDate, String statisticMessage) {
        if (statisticMessagesCache.containsKey(chatId)) {
            statisticMessagesCache.get(chatId).put(statisticDate, statisticMessage);
        } else {
            Map<LocalDate, String> dateMessageCache = new HashMap<>();
            dateMessageCache.put(statisticDate, statisticMessage);
            statisticMessagesCache.put(chatId, dateMessageCache);
        }
    }

    public void evictCachedData(Long chatId) {
        statisticMessagesCache.remove(chatId);
    }

    public boolean hasCachedData(Long chatId, LocalDate statisticDate) {
        return statisticMessagesCache.containsKey(chatId) && statisticMessagesCache.get(chatId).containsKey(statisticDate);
    }
}
