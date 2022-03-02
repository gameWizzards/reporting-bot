package com.telegram.reporting.messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Message {
    CREATE_REPORT("Создать отчет"),

    USER_DATE_INPUT("Введите дату для создния отчета (Допустимо - день, день.месяц, день.месяц.год)"),

    CHOICE_REPORT_CATEGORY("Выберете категорию отчета"),
    REPORT_CATEGORY_ON_STORAGE("На складе"), REPORT_CATEGORY_ON_ORDER("На заказе"),
    REPORT_CATEGORY_ON_OFFICE("На офисе"), REPORT_CATEGORY_ON_COORDINATION("На координации"),

    USER_TIME_INPUT("Введите затраченное время согласно выбранной категории отчета. Формат времени - часы"),

    REQUEST_TO_ADDITIONAL_REPORT("Хотите добавить еще один отчет за выбранную дату?"),
    CONFIRM_ADDITIONAL_REPORT("Да"),
    DECLINE_ADDITIONAL_REPORT("Нет"),

    REQUEST_TO_CONFIRMATION_REPORT("Убедитесь что отчет коректный. Отправить отчет?"),
    CONFIRM_CREATION_FINAL_REPORT("Отправить отчет"),
    DECLINE_CREATION_FINAL_REPORT("Отменить отчет"),

    ADD_NEW_USER("Добавить пользователя"),
    CANCEL("Отмена"),
    RETURN_TO_MAIN_MENU("Вернутся в меню"),

    SEPARATOR(".\n[º_º]\b-\bREPORTING BOT\b-\b[º_º]\n."),

    DELETE_REPORT("Удалить отчет"), UPDATE_REPORT("Изменить отчет")
    ;
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
        return Arrays.asList(CREATE_REPORT, DELETE_REPORT, UPDATE_REPORT);
    }

    public String text() {
        return text;
    }
}