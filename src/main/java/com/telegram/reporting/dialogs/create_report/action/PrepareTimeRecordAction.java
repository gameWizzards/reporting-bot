package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PrepareTimeRecordAction implements Action<CreateReportState, MessageEvent> {

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String time = (String) variables.get(ContextVariable.REPORT_TIME);
        String note = (String) variables.get(ContextVariable.REPORT_NOTE);
        String categoryName = (String) variables.get(ContextVariable.REPORT_CATEGORY_TYPE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        List<TimeRecordTO> trTOs = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        TimeRecordTO timeRecord = new TimeRecordTO();
        timeRecord.setHours(Integer.parseInt(time));
        timeRecord.setNote(note);
        timeRecord.setCategoryName(categoryName);
        timeRecord.setCreated(LocalDateTime.now());

        trTOs.add(timeRecord);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);

        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);
    }
}
