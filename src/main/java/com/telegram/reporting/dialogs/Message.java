package com.telegram.reporting.dialogs;

public enum Message {

    //create report dialog
    USER_DATE_INPUT_CREATE_REPORT("Введи дату для создния отчета (Допустимо - день, день.месяц, день.месяц.год)"),
    USER_TIME_INPUT("Введи затраченное время согласно выбранной категории отчета. Формат времени - часы"),
    REQUEST_ADD_NOTE_REPORT("Добавь примечание к отчету. (Опционально)"),
    REQUEST_ADDITIONAL_REPORT("Хочешь добавить еще один отчет за выбранную дату?"),
    REQUEST_CONFIRMATION_REPORT("Убедись что отчет коректный. Отправить отчет?"),

    //delete report dialog
    REQUEST_DELETE_CONFIRMATION_REPORT("Уверен что хочешь удалить этот отчет?"),

    // general
    USER_DATE_INPUT_GENERAL("Введи дату для поиска отчетов за этот день (Допустимо - день, день.месяц, день.месяц.год)"),
    GENERAL_ERROR_MESSAGE("Упс))) Что-то пошло не так, попробуй начать новый диалог."),
    CHOICE_REPORT_CATEGORY("Выбери категорию отчета"),
    ;

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}