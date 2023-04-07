package com.telegram.reporting.service.impl;

import com.telegram.reporting.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final CacheManager cacheManager;

    @Override
    public void evictCache(String cacheName, String key) {
        if (Objects.isNull(cacheName) || Objects.isNull(key)) {
            throw new IllegalArgumentException("Can't find cacheName or key for cache evicting. CacheName: %s, key: %s".formatted(cacheName, key));
        }
        Optional.ofNullable(cacheManager.getCache(cacheName))
                .ifPresent(cache -> cache.evict(key));
    }

    @Override
    public void evictCache(String cacheName, Long chatId, LocalDate dateKeyPart) {
        if (Objects.isNull(dateKeyPart)) {
            throw new IllegalArgumentException("Can't find date for cache evicting. ChatId: " + chatId);
        }

        String cacheKey = chatId.toString() + dateKeyPart.getMonthValue() + dateKeyPart.getYear();
        evictCache(cacheName, cacheKey);
    }
}
