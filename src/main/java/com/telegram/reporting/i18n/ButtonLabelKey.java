package com.telegram.reporting.i18n;

import java.util.Arrays;

public enum ButtonLabelKey implements I18nKey {
    // admin dialogs
    ASD_ADMIN_START_SUB_DIALOG("admin.start.sub-root.menu"),

    ALU_START_DIALOG("admin.list-users.start.dialog"),
    ALU_USER_STATUS_ACTIVE("admin.list-users.active.users"),
    ALU_USER_STATUS_NOT_VERIFIED("admin.list-users.not.verified.users"),
    ALU_USER_STATUS_DELETED("admin.list-users.deleted.users"),

    // common
    COMMON_YES("common.yes"),
    COMMON_NO("common.no"),
    COMMON_CANCEL("common.cancel"),
    COMMON_RETURN_MAIN_MENU("common.return.root.menu"),
    COMMON_RETURN_MANAGER_MENU("common.return.manager.menu"),
    COMMON_RETURN_ADMIN_MENU("common.return.admin.menu"),
    COMMON_INPUT_NEW_DATE("common.input.date"),
    COMMON_LIST_TIME_RECORDS("common.time-records.list"),
    COMMON_SHARE_PHONE("common.share.contact"),

    COMMON_CATEGORY_ON_STORAGE("common.category.on.storage"), COMMON_CATEGORY_ON_ORDER("common.category.on.order"),
    COMMON_CATEGORY_ON_OFFICE("common.category.on.office"), COMMON_CATEGORY_ON_COORDINATION("common.category.on.coordination"),

    //general dialogs
    GCR_START_DIALOG("general.create-report.start.dialog"),
    GCR_SKIP_NOTE("general.create-report.skip.note"),
    GCR_SEND_REPORT("general.create-report.send.report"),

    GDR_START_DIALOG("general.delete-report.start.dialog"),
    GDR_CONFIRM_DELETE_TIME_RECORD("general.delete-report.confirm.deleting.report"),

    GER_START_DIALOG("general.edit-report.start.dialog"),
    GER_SPEND_TIME("general.edit-report.spent.time"),
    GER_CATEGORY("general.edit-report.report.category"),
    GER_NOTE("general.edit-report.note"),
    GER_CONFIRM_EDIT_ADDITIONAL_DATA("general.edit-report.confirm.editing.additional.data"),
    GER_DECLINE_EDIT_ADDITIONAL_DATA("general.edit-report.decline.editing.additional.data"),
    GER_APPLY_DATA_CHANGES("general.edit-report.apply.data.changes"),

    GL_START_DIALOG("general.language.start.dialog"),
    GL_UA_LOCALE("general.language.ua.locale"),
    GL_RU_LOCALE("general.language.ru.locale"),

    GS_START_DIALOG("general.statistic.start.dialog"),
    GS_PREVIOUS_MONTH_STATISTIC("general.statistic.previous.month.statistic"),
    GS_CURRENT_MONTH_STATISTIC("general.statistic.current.month.statistic"),


    // manager dialogs
    MSD_MANAGER_START_SUB_DIALOG("manager.start.sub-root.menu"),

    MAE_START_DIALOG("manager.add-employee.start.dialog"),

    MES_START_DIALOG("manager.employee-statistic.start.dialog"),
    MES_CHOICE_ANOTHER_EMPLOYEE("manager.employee-statistic.choose.employee"),
    MES_LOCK_EDIT_REPORT_DATA("manager.employee-statistic.lock.edit.report.data"),
    MES_UNLOCK_EDIT_REPORT_DATA("manager.employee-statistic.unlock.edit.report.data"),

    MESTATUS_START_DIALOG("manager.employee-status.start.dialog"),
    MESTATUS_CHOICE_ANOTHER_LIST_EMPLOYEES("manager.employee-status.choose.another.list"),
    MESTATUS_CHANGE_EMPLOYEE_STATUS("manager.employee-status.change.employee.status"),
    MESTATUS_CHANGE_EMPLOYEE_ROLE("manager.employee-status.change.employee.role"),
    MESTATUS_ACTIVATE_EMPLOYEE("manager.employee-status.active.employee"),
    MESTATUS_DELETE_EMPLOYEE("manager.employee-status.delete.employee"),
    MESTATUS_ADD_MANAGER_ROLE("manager.employee-status.add.manager.role"),
    MESTATUS_REMOVE_MANAGER_ROLE("manager.employee-status.remove.manager.role"),

    ;

    private final String value;

    ButtonLabelKey(String value) {
        this.value = value;
    }

    public static ButtonLabelKey getByKey(String key) {
        return Arrays.stream(values())
                .filter(buttonLabelKey -> buttonLabelKey.value().equals(key))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String value() {
        return value;
    }
}