package com.telegram.reporting.listener.event;

import com.telegram.reporting.listener.EventType;
import com.telegram.reporting.domain.TimeRecord;
import lombok.Getter;
import lombok.ToString;

@ToString
public class ReportUpdateEvent {
    private EventType eventType;
    private Long chatId;
    @Getter
    private Long reportId;
    private Long timeRecordId;
    private Long categoryId;
    private Integer workTime;

    private ReportUpdateEvent() {
    }

    public ReportUpdateEvent(EventType eventType, TimeRecord timeRecord) {
        this.eventType = eventType;
        this.chatId = timeRecord.getReport().getUser().getChatId();
        this.reportId = timeRecord.getReport().getId();
        this.timeRecordId = timeRecord.getId();
        this.categoryId = timeRecord.getCategory().getId();
        this.workTime = timeRecord.getHours();
    }
}
