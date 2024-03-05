package com.telegram.reporting.i18n;

public enum MessageKey implements I18nKey {

    // admin dialogs
    ASD_START_SUB_DIALOG_FLOW("admin.sub-dialog.start.flow"),

    ALU_EMPTY_USER_LIST("admin.list-users.empty.user.list"),
    ALU_SHOW_ANOTHER_CATEGORY_USERS("admin.list-users.show.another.category.users"),

    // common
    COMMON_CATEGORY_ON_COORDINATION_DESCRIPTION("common.category.on.coordination.description"),
    COMMON_CATEGORY_ON_OFFICE_DESCRIPTION("common.category.on.office.description"),
    COMMON_CATEGORY_ON_ORDER_DESCRIPTION("common.category.on.order.description"),
    COMMON_CATEGORY_ON_STORAGE_DESCRIPTION("common.category.on.storage.description"),
    COMMON_CHOOSE_AVAILABLE_REPORTS("common.choose.available.reports"),
    COMMON_CHOOSE_CATEGORY("common.choose.category"),
    COMMON_BASE_TIME_RECORD_MESSAGE("common.base.time.record.message"),
    COMMON_DATE_ACCEPTED("common.date.accepted"),
    COMMON_EMPLOYEE_STATUS_ACTIVATED("common.employee.status.activated"),
    COMMON_EMPLOYEE_STATUS_DELETED("common.employee.status.deleted"),
    COMMON_NOTE_ACCEPTED("common.note.accepted"),
    COMMON_REPORT_ABSENT("common.report.absent"),
    COMMON_REPORT_WITHOUT_NOTE("common.report.without.note"),
    COMMON_REQUEST_CHOOSE_ANOTHER_EMPLOYEE("common.request.choose.another.employee"),
    COMMON_REQUEST_GO_USER_CHAT("common.request.go.user.chat"),
    COMMON_REQUEST_INPUT_DATE("common.request.input.date"),
    COMMON_TIME_ACCEPTED("common.time.accepted"),
    COMMON_TOTAL_MONTH_STATISTIC_MESSAGE("common.statistic.total.month.statistic.message"),
    COMMON_USER_CHAT_LINK("common.user.chat.link"),
    COMMON_USER_NOT_AUTHORIZED("common.user.not.authorized"),
    COMMON_WARNING_SOMETHING_GOING_WRONG("common.warning.something.going.wrong"),
    COMMON_WARNING_USER_INPUT_INSTEAD_BTN("common.warning.user.input.instead.btn"),
    COMMON_WARNING_CLICK_ANOTHER_DIALOG_BUTTON("common.warning.click.another.dialog.btn"),
    COMMON_WARNING_USER_STATUS_DELETED("common.warning.user.status.deleted"),
    COMMON_EMPLOYEE_ROLE("common.employee.role"),
    COMMON_MANAGER_ROLE("common.manager.role"),
    COMMON_TARIFF_EDITOR_ROLE("common.tariff.editor.role"),
    COMMON_ADMIN_ROLE("common.admin.role"),
    COMMON_WARNING_ROLE_UNMAPPED("common.warning.role.unmapped"),

    //general dialogs
    GCR_ALL_CATEGORIES_COMPLETED("general.create-report.all.categories.completed"),
    GCR_ALL_CATEGORIES_OCCUPIED("general.create-report.all.categories.occupied"),
    GCR_DECLINE_CREATING_REPORT("general.create-report.decline.creating.report"),
    GCR_DISPLAY_CHOSEN_CATEGORY("general.create-report.display.chosen.category"),
    GCR_INPUT_DATE_TO_CREATE_REPORT("general.create-report.input.date"),
    GCR_INPUT_SPEND_TIME("general.create-report.input.spend.time"),
    GCR_PREVIOUSLY_CREATED_REPORTS("general.create-report.previously.created.reports"),
    GCR_REPORT_SUCCESSFUL_CREATED("general.create-report.report.successful.created"),
    GCR_REQUEST_ADD_NOTE("general.create-report.request.add.note"),
    GCR_REQUEST_ADDITIONAL_REPORT("general.create-report.request.additional.report"),
    GCR_REQUEST_CONFIRMATION_SEND_REPORT("general.create-report.request.confirmation.send.report"),
    GCR_REQUEST_SEND_REPORT("general.create-report.request.send.report"),

    GDR_FAILURE_DELETING_REPORT("general.delete-report.failure.deleting.report"),
    GDR_REPORT_SUCCESSFUL_DELETED("general.delete-report.report.successful.deleted"),
    GDR_REQUEST_DELETE_REPORT("general.delete-report.request.delete.report"),
    GER_CANCEL_EDITING_TIP("general.edit-report.cancel.editing.tip"),
    GER_CHOOSE_EDITING_DATA("general.edit-report.choose.editing.data"),
    GER_REPORT_SUCCESSFUL_UPDATED("general.edit-report.report.successful.updated"),
    GER_REQUEST_CHANGE_CATEGORY("general.edit-report.request.change.category"),
    GER_REQUEST_CHANGE_HOURS("general.edit-report.request.change.hours"),
    GER_REQUEST_CHANGE_NOTE("general.edit-report.request.change.note"),
    GER_REQUEST_CHANGE_REPORT("general.edit-report.request.change.report"),
    GER_REQUEST_EDIT_ADDITIONAL_REPORT("general.edit-report.request.edit.additional.report"),
    GER_REQUEST_SEND_CHANGES("general.edit-report.request.send.changes"),
    GER_WARNING_EDITING_ALL_OCCUPIED_CATEGORIES("general.edit-report.warning.editing.all.occupied.categories"),

    GL_CHOICE_LANGUAGE("general.language.choice.locale"),
    GL_RESULT_CHANGING_LANGUAGE("general.language.result.changing.locale"),

    GS_ABSENT_REPORTS("general.statistic.absent.reports"),
    GS_BASE_STATISTIC_MESSAGE("general.statistic.base.statistic.message"),
    GS_BASE_STATISTIC_SUB_MESSAGE("general.statistic.base.statistic.sub.message"),
    GS_BY_CATEGORIES("general.statistic.by.categories"),
    GS_HOURS_BY_CATEGORY_MESSAGE("general.statistic.total.hours.by.category.message"),
    GS_REQUEST_CURRENT_MONTH_STATISTIC("general.statistic.request.current.month.statistic"),
    GS_REQUEST_PREVIOUS_MONTH_STATISTIC("general.statistic.request.previous.month.statistic"),

    // common guards
    GUARD_WARNING_DATE_BEFORE_AUTH("guard.warning.date.before.authorization"),
    GUARD_WARNING_NON_EXISTENT_DATE("guard.warning.non.existent.date"),
    GUARD_WARNING_DUPLICATE_ADDING_PHONE("guard.warning.duplicate.adding.phone"),
    GUARD_WARNING_FAILED_DISPLAY_USERNAME("guard.warning.failed.display.username"),
    GUARD_WARNING_PERIOD_LOCKED_TO_EDITING("guard.warning.period.locked.to.editing"),
    GUARD_WARNING_SHORT_NOTE_SIZE("guard.warning.short.note.size"),
    GUARD_WARNING_WRONG_DATE_FORMAT("guard.warning.wrong.date.format"),
    GUARD_WARNING_WRONG_MONTH_FORMAT("guard.warning.wrong.month.format"),
    GUARD_WARNING_WRONG_PHONE_FORMAT("guard.warning.wrong.phone.format"),
    GUARD_WARNING_WRONG_TIME_FORMAT("guard.warning.wrong.time.format"),

    //manager dialogs
    MSD_START_SUB_DIALOG_FLOW("manager.sub-dialog.start.flow"),

    MAE_PHONE_INPUT_TIP("manager.add-employee.phone.input.tip"),
    MAE_USER_SUCCESSFUL_ADDED("manager.add-employee.user.successful.added"),

    MES_CHOOSE_EMPLOYEE_SHOW_STATISTIC("manager.employee-statistic.choose.employee.show.statistic"),
    MES_EMPLOYEE_MONTH_STATISTIC_MESSAGE("manager.employee-statistic.employee.month.statistic.message"),
    MES_INPUT_MONTH("manager.employee-statistic.input.month"),
    MES_LOCK_EDITING_TIP("manager.employee-statistic.lock.editing.tip"),
    MES_PERIOD_LOCKED_TO_EDITING("manager.employee-statistic.period.locked.to.editing"),
    MES_PERIOD_SUCCESSFUL_LOCKED("manager.employee-statistic.period.successful.locked"),
    MES_PERIOD_SUCCESSFUL_UNLOCKED("manager.employee-statistic.period.successful.unlocked"),
    MES_PERIOD_UNLOCKED_TO_EDITING("manager.employee-statistic.period.unlocked.to.editing"),
    MES_REQUEST_CHANGING_LOCK_STATUS("manager.employee-statistic.request.changing.lock.status"),
    MES_REQUEST_SHOW_ANOTHER_EMPLOYEE_STATISTIC("manager.employee-statistic.request.show.another.employee.statistic"),
    MES_WARNING_STATISTIC_NOT_EXISTS("manager.employee-statistic.warning.statistic.not.exists"),

    MESTATUS_CHOOSE_ANOTHER_EMPLOYEE_LIST("manager.employee-status.choose.another.employee.list"),
    MESTATUS_CHOOSE_AVAILABLE_EDIT_DATA("manager.employee-status.choose.available.edit.data"),
    MESTATUS_CHOOSE_EMPLOYEE_EDIT_STATUS("manager.employee-status.choose.employee.edit.status"),
    MESTATUS_CHOOSE_EMPLOYEE_LIST("manager.employee-status.choose.employee.list"),
    MESTATUS_EMPLOYEE_INFO_STATUS_MESSAGE("manager.employee-status.employee.status.info.message"),
    MESTATUS_EMPLOYEE_ROLES_SUCCESSFUL_UPDATED("manager.employee-status.employee.roles.successful.updated"),
    MESTATUS_EMPLOYEE_STATUS_SUCCESSFUL_UPDATED("manager.employee-status.employee.status.successful.updated"),
    MESTATUS_LIST_EMPLOYEES_STATUS("manager.employee-status.list.employees.status"),
    MESTATUS_REQUEST_CHANGE_ADDITIONAL_EMPLOYEE_DATA("manager.employee-status.request.change.additional.employee.data"),
    MESTATUS_REQUEST_CHANGE_EMPLOYEE_DATA("manager.employee-status.request.change.employee.data"),
    MESTATUS_REQUEST_CHANGE_EMPLOYEE_ROLE("manager.employee-status.request.change.employee.role"),
    MESTATUS_REQUEST_CHANGE_EMPLOYEE_STATUS("manager.employee-status.request.change.employee.status"),
    MESTATUS_WARNING_CANNOT_CHANGE_ROLE("manager.employee-status.warning.cannot.change.role"),

    //tariff dialogs
    TSD_START_SUB_DIALOG_FLOW("tariff.sub-dialog.start.flow"),
    TLT_COMPANY_TARIFFS("tariff.list-tariffs.company.tariffs"),
    TLT_OVERRIDDEN_TARIFFS_BY_CATEGORY("tariff.list-tariffs.overridden.tariffs.by.category"),
    TLT_TARIFF_ROW("tariff.list-tariffs.tariff.row"),
    TLT_SHOW_OVERRIDDEN_TARIFFS("tariff.list-tariffs.show.overridden.tariffs"),
    TLT_NO_OVERRIDDEN_TARIFFS("tariff.list-tariffs.no.overridden.tariffs"),
    TLT_CHOOSE_OVERRIDDEN_TARIFF_BY_CATEGORY("tariff.list-tariffs.choose.overridden.tariff.by.category"),
    TLT_CHOOSE_OVERRIDDEN_TARIFF_BY_EMPLOYEE("tariff.list-tariffs.choose.overridden.tariff.by.employee"),

    //pre dialogs
    PD_ACCOUNT_DELETED("predialog.account.deleted"),
    PD_ACCOUNT_NOT_FOUND("predialog.account.not.found"),
    PD_BOT_FIRST_GREETING("predialog.bot.first.greeting"),
    PD_FAILED_ACCOUNT_CHECKING("predialog.failed.account.checking"),
    PD_FAILED_CONTACT_CHECKING("predialog.failed.contact.checking"),
    PD_START_FLOW_MESSAGE("predialog.start.flow"),
    PD_SUCCESS_CONTACT_SHARING("predialog.success.contact.sharing"),
    PD_UNKNOWN_COMMAND_CHOSEN("predialog.unknown.command.chosen"),

    //notification
    NOTIFICATION_REPORT_REMAINDER_BEFORE_WEEKEND("notification.report.remainder.before.weekend"),
    NOTIFICATION_REPORT_REMAINDER_AFTER_WEEKEND("notification.report.remainder.after.weekend"),

    ;
    private final String value;

    MessageKey(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return this.value;
    }
}