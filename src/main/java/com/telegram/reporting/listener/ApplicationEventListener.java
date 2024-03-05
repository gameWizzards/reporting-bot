package com.telegram.reporting.listener;

import com.telegram.reporting.listener.event.ReportUpdateEvent;
import com.telegram.reporting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventListener {
    private final ReportRepository reportRepository;

    @Async
    @EventListener(ReportUpdateEvent.class)
    public void handleReportUpdateEvent(ReportUpdateEvent event) {
        log.info("Report was update: {}", event);
        reportRepository.setLastUpdateTime(event.getReportId(), LocalDateTime.now());
    }
}