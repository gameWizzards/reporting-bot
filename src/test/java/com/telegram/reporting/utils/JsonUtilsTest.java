package com.telegram.reporting.utils;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilsTest {

    @Test
    public void serializeItem_inputValidObject() {
        String timeRecordNote = "Time record note";

        TimeRecordTO timeRecordTO = new TimeRecordTO();
        timeRecordTO.setNote(timeRecordNote);
        timeRecordTO.setHours(2);
        timeRecordTO.setCreated(LocalDateTime.now());
        timeRecordTO.setCategoryName("Category name");


        String json = JsonUtils.serializeItem(timeRecordTO);

        assertTrue(json.contains(timeRecordNote));
    }

    @Test
    public void serializeItem_inputNull_returnEmptyString() {
        String json = JsonUtils.serializeItem(null);

        assertTrue(json.isEmpty());
    }

    @Test
    void deserializeListItems_inputValidData() {
        String json = """
                [{"id":null,"hours":2,"created":"08-03-2022 17:47:23","note":"Time record note","category":{"name":"Category name","description":"Category description"}},
                {"id":null,"hours":4,"created":"09-03-2022 12:20:20","note":"Second time record note ","category":{"name":"Second category name","description":"Second category description"}}
                ]
                """;
        List<TimeRecordTO> listItems = JsonUtils.deserializeListItems(json, TimeRecordTO.class);
        TimeRecordTO timeRecordTO = listItems.get(0);

        assertNotNull(timeRecordTO);
    }

    @Test
    public void deserializeListItems_inputNull_returnEmptyList() {
        String json = null;
        List<TimeRecordTO> listItems = JsonUtils.deserializeListItems(json, TimeRecordTO.class);

        assertTrue(listItems.isEmpty());
    }

}