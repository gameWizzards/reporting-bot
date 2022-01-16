package com.telegram.reporting.bot;

import java.util.Arrays;
import java.util.List;

public enum MessageEvent {
    CREATE_REPORT_EVENT("Создать отчет"),
    USER_DATE_INPUT("Введите дату"),
    VALID_DATE("dummy1"),
    INVALID_DATE("dummy2"),
    CHOICE_REPORT_CATEGORY("Выберите категорию"),
    USER_TIME_INPUT("dummy3"),
    VALID_TIME("dummy3"),
    INVALID_TIME("dummy4"),
    CONFIRM_ADDITIONAL_REPORT("что это такое?"),
    DECLINE_ADDITIONAL_REPORT("что это такое??"),
    CONFIRM_CREATION_FINAL_REPORT("Подтвердить"),
    DECLINE_CREATION_FINAL_REPORT("Отменить"),

    UPDATE_REPORT_EVENT("Изменить отчет"),

    DELETE_REPORT_EVENT("Удалить отчет"),

    ADD_NEW_USER("Добавить пользователя"),

    CANCEL("Отмена"),
    RETURN_TO_MAIN_MENU("Вернутся в меню");
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
