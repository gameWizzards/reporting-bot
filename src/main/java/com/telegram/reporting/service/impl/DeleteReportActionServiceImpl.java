package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.delete_report.DeleteReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.DeleteReportActionService;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteReportActionServiceImpl implements DeleteReportActionService {
    private final SendBotMessageService sendBotMessageService;

    public DeleteReportActionServiceImpl(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void requestInputDate(StateContext<DeleteReportState, MessageEvent> context) {
        log.warn("requestInputDate");
    }

    @Override
    public void requestDeleteConfirmation(StateContext<DeleteReportState, MessageEvent> context) {
        log.warn("requestDeleteConfirmation");
    }

    @Override
    public void removeTimeRecord(StateContext<DeleteReportState, MessageEvent> context) {
        log.warn("requestDeleteConfirmation");
    }
}
