package com.telegram.reporting.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ButtonValue {
    //create report dialog
    CREATE_REPORT_START_DIALOG("Создать отчет"),

    REPORT_CATEGORY_ON_STORAGE("На складе"), REPORT_CATEGORY_ON_ORDER("На заказе"),
    REPORT_CATEGORY_ON_OFFICE("На офисе"), REPORT_CATEGORY_ON_COORDINATION("На координации"),

    SKIP_NOTE("Пропустить примечание"),
    CONFIRM_CREATION_FINAL_REPORT("Отправить отчет"),

    //delete report dialog
    DELETE_REPORT_START_DIALOG("Удалить отчет"),
    CONFIRM_DELETE_TIME_RECORD("Удалить запись"),

    // update report dialog
    UPDATE_REPORT_START_DIALOG("Изменить отчет"),

    // add new user dialog
    ADD_NEW_USER_START_DIALOG("Добавить пользователя"),

    // general
    YES("Да"),
    NO("Нет"),
    CANCEL("Отмена"),
    MAIN_MENU("Главное меню"),
    INPUT_NEW_DATE("Ввести новую дату");

    private final String text;

    ButtonValue(String text) {
        this.text = text;
    }

    public static Optional<ButtonValue> getByText(String text) {
        return Arrays.stream(values())
                .filter(message -> message.text().equals(text))
                .findFirst();
    }

    public static List<ButtonValue> startMessages() {
        return Arrays.asList(CREATE_REPORT_START_DIALOG, DELETE_REPORT_START_DIALOG, UPDATE_REPORT_START_DIALOG);
    }

    public String text() {
        return text;
    }
}