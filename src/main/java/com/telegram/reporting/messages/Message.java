package com.telegram.reporting.messages;

import java.util.Arrays;
import java.util.Optional;

public enum Message {
    CREATE_REPORT("Создать отчет"),
    USER_DATE_INPUT("Введите дату"),
    CHOICE_REPORT_CATEGORY("Выберите категорию"),
    USER_TIME_INPUT("dummy3"),
    VALID_TIME("dummy3"),
    INVALID_TIME("dummy4"),
    CONFIRM_ADDITIONAL_REPORT("что это такое?"),
    DECLINE_ADDITIONAL_REPORT("что это такое??"),
    CONFIRM_CREATION_FINAL_REPORT("Подтвердить"),
    DECLINE_CREATION_FINAL_REPORT("Отменить"),
    UPDATE_REPORT("Изменить отчет"),
    DELETE_REPORT("Удалить отчет"),
    ADD_NEW_USER("Добавить пользователя"),
    CANCEL("Отмена"),
    RETURN_TO_MAIN_MENU("Вернутся в меню");

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public static Optional<Message> getByText(String text) {
        return Arrays.stream(values())
                .filter(message -> message.text().equals(text))
                .findFirst();
    }

    public String text() {
        return text;
    }
}