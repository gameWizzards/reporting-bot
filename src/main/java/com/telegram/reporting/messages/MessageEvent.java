package com.telegram.reporting.messages;

import java.util.Arrays;
import java.util.List;

public enum MessageEvent {
    CREATE_REPORT_EVENT(""),
    USER_DATE_INPUT(""),
    VALID_DATE(""),
    INVALID_DATE(""),
    CHOICE_REPORT_CATEGORY(""),
    USER_TIME_INPUT(""),
    VALID_TIME(""),
    INVALID_TIME(""),
    CONFIRM_ADDITIONAL_REPORT(""),
    DECLINE_ADDITIONAL_REPORT(""),
    CONFIRM_CREATION_FINAL_REPORT(""),
    DECLINE_CREATION_FINAL_REPORT(""),

    UPDATE_REPORT_EVENT(""),

    DELETE_REPORT_EVENT(""),

    ADD_NEW_USER(""),

    CANCEL("Отмена"),
    RETURN_TO_MAIN_MENU("")
            ;
    private final String message;

    MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static MessageEvent getByMessage(String message) {
        return Arrays.stream(values())
                .filter(mesEvent -> mesEvent.getMessage().equals(message))
                .findFirst()
                .orElse(null);
    }
    
    public static List<MessageEvent> getStartDialogMessages() {
        return Arrays.asList(CREATE_REPORT_EVENT, UPDATE_REPORT_EVENT, DELETE_REPORT_EVENT, ADD_NEW_USER);
    }
}
