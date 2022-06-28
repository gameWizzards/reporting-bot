package com.telegram.reporting.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ButtonValue {
    //create report dialog
    CREATE_REPORT_START_DIALOG("Создать отчет"),
    SKIP_NOTE("Пропустить примечание"),
    CONFIRM_CREATION_FINAL_REPORT("Отправить отчет"),

    //delete report dialog
    DELETE_REPORT_START_DIALOG("Удалить отчет"),
    CONFIRM_DELETE_TIME_RECORD("Удалить запись"),

    // edit report dialog
    EDIT_REPORT_START_DIALOG("Изменить отчет"),
    SPEND_TIME("Затраченное время"),
    CATEGORY("Категория рабочего времени"),
    NOTE("Примичание"),
    CONFIRM_EDIT_ADDITIONAL_DATA("Изменить еще"),
    DECLINE_EDIT_ADDITIONAL_DATA("И так сойдет"),
    APPLY_DATA_CHANGES("Применить изменения"),

    // add new user dialog
    ADD_NEW_USER_START_DIALOG("Добавить пользователя"),

    // general
    YES("Да"),
    NO("Нет"),
    CANCEL("Отмена"),
    MAIN_MENU("Главное меню"),
    INPUT_NEW_DATE("Ввести новую дату"),
    LIST_TIME_RECORDS("Список очтетов"),
    SHARE_PHONE("Поделится номером телефона"),

    REPORT_CATEGORY_ON_STORAGE("На складе"), REPORT_CATEGORY_ON_ORDER("На заказе"),
    REPORT_CATEGORY_ON_OFFICE("На офисе"), REPORT_CATEGORY_ON_COORDINATION("На координации"),
    ;

    private final String text;

    ButtonValue(String text) {
        this.text = text;
    }

    public static Optional<ButtonValue> getByText(String text) {
        return Arrays.stream(values())
                .filter(message -> message.text().equals(text))
                .findFirst();
    }

    public static List<ButtonValue> startDialogButtons() {
        return Arrays.asList(CREATE_REPORT_START_DIALOG, DELETE_REPORT_START_DIALOG, EDIT_REPORT_START_DIALOG);
    }

    public static List<ButtonValue> categoryButtons() {
        return Arrays.asList(REPORT_CATEGORY_ON_STORAGE, REPORT_CATEGORY_ON_ORDER,
                REPORT_CATEGORY_ON_OFFICE, REPORT_CATEGORY_ON_COORDINATION);
    }

    public String text() {
        return text;
    }
}