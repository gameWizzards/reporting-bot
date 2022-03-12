package com.telegram.reporting.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.telegram.reporting.exception.HandleTimeRecordException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static String serializeItem(Object item) {
        if (item == null) {
            return "";
        }
        try {
            return mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new HandleTimeRecordException(e.getMessage(), e.getCause());
        }
    }

    public static <T> T deserializeItem(String item, Class<T> clazz) {
        try {
            if (item == null) {
                return clazz.getDeclaredConstructor().newInstance();
            }
            return mapper.readValue(item, clazz);
        } catch (JsonProcessingException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new HandleTimeRecordException(e.getMessage(), e.getCause());
        }
    }

    public static <T> List<T> deserializeListItems(String item, Class<T> clazz) {
        if (item == null) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(item, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new HandleTimeRecordException(e.getMessage(), e.getCause());
        }
    }
}
