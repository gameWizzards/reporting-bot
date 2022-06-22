package com.telegram.reporting.messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Message {
    CREATE_REPORT_START_MESSAGE("Создать отчет"),

    USER_DATE_INPUT("Введи дату для создния отчета (Допустимо - день, день.месяц, день.месяц.год)"),

    CHOICE_REPORT_CATEGORY("Выбери категорию отчета"),
    REPORT_CATEGORY_ON_STORAGE("На складе"), REPORT_CATEGORY_ON_ORDER("На заказе"),
    REPORT_CATEGORY_ON_OFFICE("На офисе"), REPORT_CATEGORY_ON_COORDINATION("На координации"),

    USER_TIME_INPUT("Введи затраченное время согласно выбранной категории отчета. Формат времени - часы"),

    REQUEST_ADDITIONAL_REPORT("Хочешь добавить еще один отчет за выбранную дату?"),
    CONFIRM_ADDITIONAL_REPORT("Да"),
    DECLINE_ADDITIONAL_REPORT("Нет"),

    REQUEST_ADD_NOTE_REPORT("Добавь примечание к отчету. (Опционально)"),
    SKIP_NOTE("Пропустить примечание"),

    REQUEST_CONFIRMATION_REPORT("Убедись что отчет коректный. Отправить отчет?"),
    CONFIRM_CREATION_FINAL_REPORT("Отправить отчет"),

    ADD_NEW_USER("Добавить пользователя"),
    CANCEL("Отмена"),
    RETURN_TO_MAIN_MENU("Вернутся в меню"),

    DELETE_REPORT_START_MESSAGE("Удалить отчет"),
    REQUEST_DELETE_CONFIRMATION_REPORT("Вы точно хотите удалить отчет?"),
    CONFIRM_DELETE_TIME_RECORD("Удалить отчет"),

    UPDATE_REPORT_START_MESSAGE("Изменить отчет"),

    MAIN_MENU("Главное меню");
    private final String text;

    Message(String text) {
        this.text = text;
    }

    public static Optional<Message> getByText(String text) {
        return Arrays.stream(values())
                .filter(message -> message.text().equals(text))
                .findFirst();
    }

    public static List<Message> startMessages() {
        return Arrays.asList(CREATE_REPORT_START_MESSAGE, DELETE_REPORT_START_MESSAGE, UPDATE_REPORT_START_MESSAGE);
    }

    public String text() {
        return text;
    }
}