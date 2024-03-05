package com.telegram.reporting.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

public enum EventPublisher {
    INSTANCE;

    private static ApplicationEventPublisher eventPublisher;

    public void publishEvent(Object event) {
        Objects.requireNonNull(event, "Event must not be null");
         eventPublisher.publishEvent(event);
    }

    @Component
    public static class TimeRecordEventPublisherInitializer {
        @Autowired
        public TimeRecordEventPublisherInitializer(ApplicationEventPublisher eventPublisher) {
            EventPublisher.eventPublisher = eventPublisher;
        }
    }
}
