package com.telegram.reporting.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ButtonValue {
    //create report dialog
    CREATE_REPORT_START_DIALOG("\u2705 Создать отчет"),
    SKIP_NOTE("Пропустить примечание"),
    CONFIRM_CREATION_FINAL_REPORT("Отправить отчет"),

    //delete report dialog
    DELETE_REPORT_START_DIALOG("\uE333 Удалить отчет"),
    CONFIRM_DELETE_TIME_RECORD("Удалить запись"),

    // edit report dialog
    EDIT_REPORT_START_DIALOG("\u270F Изменить отчет"),
    SPEND_TIME("Затраченное время"),
    CATEGORY("Категория рабочего времени"),
    NOTE("Примичание"),
    CONFIRM_EDIT_ADDITIONAL_DATA("Изменить еще"),
    DECLINE_EDIT_ADDITIONAL_DATA("И так сойдет"),
    APPLY_DATA_CHANGES("Применить изменения"),

    // statistic dialog
    STATISTIC_START_DIALOG("\uD83D\uDCC8 Статистика"),
    PREVIOUS_MONTH_STATISTIC("Показать за прошлый месяц"),

    // employee statistic dialog
    EMPLOYEE_STATISTIC_START_DIALOG("\uD83D\uDCCA Стат. сотрудников"),
    CHOICE_ANOTHER_EMPLOYEE("Выбрать другого сотрудника"),
    LOCK_EDIT_REPORT_DATA("\uD83D\uDD34 Заблокировать период"),
    UNLOCK_EDIT_REPORT_DATA("\uD83D\uDFE2 Открыть доступ"),

    // list users dialog
    LIST_USERS_START_DIALOG("\uD83D\uDCCB Список пользователей"),
    USER_STATUS_ACTIVE("Активные"),
    USER_STATUS_NOT_VERIFIED("На верифифкации"),
    USER_STATUS_DELETED("Удаленные"),

    // add new user dialog
    ADD_NEW_USER_START_DIALOG("Добавить пользователя"),

    // general
    YES("Да"),
    NO("Нет"),
    CANCEL("Отмена"),
    RETURN_MAIN_MENU("Главное меню"),
    RETURN_MANAGER_MENU("Меню менеджера"),
    RETURN_ADMIN_MENU("Меню администратора"),
    INPUT_NEW_DATE("Ввести новую дату"),
    LIST_TIME_RECORDS("Список очтетов"),
    SHARE_PHONE("Поделится номером телефона"),

    REPORT_CATEGORY_ON_STORAGE("На складе"), REPORT_CATEGORY_ON_ORDER("На заказе"),
    REPORT_CATEGORY_ON_OFFICE("На офисе"), REPORT_CATEGORY_ON_COORDINATION("На координации"),

    MANAGER_MENU_START_DIALOG("\uD83D\uDC68\u200D\uD83D\uDCBC Меню менеджера"),
    ADMIN_MENU_START_DIALOG("\uD83D\uDCBB Меню администратора");

    private final String text;

    ButtonValue(String text) {
        this.text = text;
    }

    public static Optional<ButtonValue> getByText(String text) {
        return Arrays.stream(values())
                .filter(message -> message.text().equals(text))
                .findFirst();
    }

    public static List<ButtonValue> categoryButtons() {
        return Arrays.asList(REPORT_CATEGORY_ON_STORAGE, REPORT_CATEGORY_ON_ORDER,
                REPORT_CATEGORY_ON_OFFICE, REPORT_CATEGORY_ON_COORDINATION);
    }

    public String text() {
        return text;
    }
}