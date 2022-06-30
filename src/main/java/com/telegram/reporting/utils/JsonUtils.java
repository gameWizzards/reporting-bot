package com.telegram.reporting.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.telegram.reporting.exception.JsonUtilsException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static String serializeItem(Object item) {
        try {
            if (item == null) {
                throw new NullPointerException("Can't serialize NULL");
            }

            return mapper.writeValueAsString(item);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new JsonUtilsException(e.getMessage(), e.getCause());
        }
    }

    public static <T> T deserializeItem(String json, Class<T> clazz) {
        try {
            checkInputBeforeDeserialize(json, clazz);

            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new JsonUtilsException(e.getMessage(), e.getCause());
        }
    }

    public static <T> List<T> deserializeListItems(String json, Class<T> clazz) {
        try {
            checkInputBeforeDeserialize(json, clazz);

            return mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException | NullPointerException e) {
            throw new JsonUtilsException(e.getMessage(), e.getCause());
        }
    }

    private static <T> void checkInputBeforeDeserialize(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            throw new NullPointerException("Can't deserialize json without value");
        }
        if (clazz == null) {
            throw new NullPointerException("Can't deserialize json without class type. Json: %s".formatted(json));
        }
    }
}
