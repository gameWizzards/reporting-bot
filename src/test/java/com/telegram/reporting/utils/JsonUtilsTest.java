package com.telegram.reporting.utils;

import com.telegram.reporting.ExceptionHelper;
import com.telegram.reporting.exception.JsonUtilsException;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Report;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class JsonUtilsTest {
    private final String expectedDeserializeNullValueExceptionMessage = "Can't deserialize json without value";
    private final String expectedDeserializeNullTypeExceptionMessage = "Can't deserialize json without class type. Json:";

    private final String timeRecordJson = """
            {"id":null,"hours":2,"created":"08.03.2022 17:47:23","note":"Time record note","category":{"name":"Category name","description":"Category description"}}
            """;

    private final String listTimeRecordsJson = """
            [{"id":null,"hours":2,"created":"08.03.2022 17:47:23","note":"Time record note","category":{"name":"Category name","description":"Category description"}},
            {"id":null,"hours":4,"created":"09.03.2022 12:20:20","note":"Second time record note ","category":{"name":"Second category name","description":"Second category description"}}
            ]
            """;

    @Test
    public void serializeItem_inputValidObject_success() {
        var timeRecordNote = "Time record note";

        var timeRecordTO = new TimeRecordTO();
        timeRecordTO.setNote(timeRecordNote);
        timeRecordTO.setHours(2);
        timeRecordTO.setCreated(LocalDateTime.now());
        timeRecordTO.setCategoryName("Category name");

        var json = JsonUtils.serializeItem(timeRecordTO);

        assertTrue(json.contains(timeRecordNote));
    }

    @Test
    public void serializeItem_inputNull_returnEmptyString() {
        var exception = assertThrows(JsonUtilsException.class, () -> JsonUtils.serializeItem(null));

        var serializeNullValueExceptionMessage = "Can't serialize NULL";
        assertTrue(exception.getMessage().contains(serializeNullValueExceptionMessage));
    }

    @Test
    public void serializeItem_unserializableObject_throwJsonUtilsException() {
        assertThrows(JsonUtilsException.class, () -> JsonUtils.serializeItem(new JsonUtils()));
    }

    @Test
    public void deserializeItem_inputValidData_success() {
        var timeRecord = JsonUtils.deserializeItem(timeRecordJson, TimeRecordTO.class);

        assertNotNull(timeRecord);
    }

    @Test
    public void deserializeItem_inputJsonNull_throwJsonUtilsException() {
        var exception = assertThrows(JsonUtilsException.class, () -> JsonUtils.deserializeItem(null, TimeRecordTO.class));
        var isContains = exception.getMessage().contains(expectedDeserializeNullValueExceptionMessage);
        assertTrue(isContains, ExceptionHelper.cannotFindExpectedErrorMessage(expectedDeserializeNullValueExceptionMessage));
    }

    @Test
    public void deserializeItem_inputTypeNull_throwJsonUtilsException() {
        var exception = assertThrows(JsonUtilsException.class, () -> JsonUtils.deserializeItem(timeRecordJson, null));
        var isContains = exception.getMessage().contains(expectedDeserializeNullTypeExceptionMessage);
        assertTrue(isContains, ExceptionHelper.cannotFindExpectedErrorMessage(expectedDeserializeNullTypeExceptionMessage));
    }

    @Test
    public void deserializeItem_incorrectInputType_throwJsonUtilsException() {
        assertThrows(JsonUtilsException.class, () -> JsonUtils.deserializeItem(timeRecordJson, Report.class));
    }

    @Test
    public void deserializeListItems_inputValidData_success() {
        var listItems = JsonUtils.deserializeListItems(listTimeRecordsJson, TimeRecordTO.class);
        var timeRecordTO = listItems.get(0);

        assertNotNull(timeRecordTO);
    }

    @Test
    public void deserializeListItems_inputJsonNull_throwJsonUtilsException() {
        var exception = assertThrows(JsonUtilsException.class, () -> JsonUtils.deserializeListItems(null, TimeRecordTO.class));
        var isContains = exception.getMessage().contains(expectedDeserializeNullValueExceptionMessage);
        assertTrue(isContains, ExceptionHelper.cannotFindExpectedErrorMessage(expectedDeserializeNullValueExceptionMessage));
    }

    @Test
    public void deserializeListItems_inputTypeNull_throwJsonUtilsException() {
        var exception = assertThrows(JsonUtilsException.class, () -> JsonUtils.deserializeListItems(listTimeRecordsJson, null));
        var isContains = exception.getMessage().contains(expectedDeserializeNullTypeExceptionMessage);
        assertTrue(isContains, ExceptionHelper.cannotFindExpectedErrorMessage(expectedDeserializeNullTypeExceptionMessage));
    }

}