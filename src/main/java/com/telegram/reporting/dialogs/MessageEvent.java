package com.telegram.reporting.dialogs;

public enum MessageEvent {
    // create report dialog
    RUN_CREATE_REPORT_DIALOG,
    CHOOSE_REPORT_CATEGORY,
    VALIDATE_USER_TIME_INPUT,
    VALIDATE_USER_NOTE_INPUT,
    CONFIRM_ADDITIONAL_REPORT,
    DECLINE_ADDITIONAL_REPORT,
    CONFIRM_CREATION_FINAL_REPORT,
    DECLINE_CREATION_FINAL_REPORT,
    GO_TO_USER_FINAL_REPORT_CONFIRMATION,

    // delete report dialog
    RUN_DELETE_REPORT_DIALOG,
    CONFIRM_DELETE_TIME_RECORD,
    DECLINE_DELETE_TIME_RECORD,

    // edit report dialog
    RUN_EDIT_REPORT_DIALOG,
    CHOOSE_EDIT_NOTE,
    CHOOSE_EDIT_CATEGORY,
    CHOOSE_EDIT_SPEND_TIME,
    CONFIRM_EDIT_DATA,
    DECLINE_EDIT_DATA,
    HANDLE_USER_CHANGE_SPEND_TIME,
    HANDLE_USER_CHANGE_CATEGORY,
    HANDLE_USER_CHANGE_NOTE,
    CONFIRM_EDIT_ADDITIONAL_DATA,
    DECLINE_EDIT_ADDITIONAL_DATA,
    CONFIRM_EDIT_ADDITIONAL_TIME_RECORD,
    DECLINE_EDIT_ADDITIONAL_TIME_RECORD,

    // statistic dialog
    RUN_STATISTIC_DIALOG,
    SHOW_PREVIOUS_MONTH_STATISTIC,
    END_STATISTIC_DIALOG,

    // add new user dialog
    RUN_ADD_NEW_USER_DIALOG,

    //general
    CHOOSE_TIME_RECORD,
    RETURN_TO_USER_DATE_INPUTTING,
    VALIDATE_USER_DATE_INPUT
}