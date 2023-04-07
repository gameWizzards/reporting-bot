package com.telegram.reporting.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache-config.caffeine")
public class CaffeineCacheConfig {

    @Setter
    private List<CacheProps> caches = new ArrayList<>();

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager();

        for (CacheProps cacheProps : caches) {
            var configs = Caffeine.newBuilder()
                    .initialCapacity(cacheProps.capacity())
                    .maximumSize(cacheProps.maxSize())
                    .expireAfterAccess(cacheProps.ttlSecond(), TimeUnit.SECONDS)
//                    .recordStats() // TODO consider to enable this for actuator
                    .build();

            cacheManager.registerCustomCache(cacheProps.name(), configs);
        }

        return cacheManager;
    }

    public record CacheProps(String name, int capacity, int maxSize, int ttlSecond) {
    }
}
