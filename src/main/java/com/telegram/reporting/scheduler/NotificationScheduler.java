package com.telegram.reporting.scheduler;

import com.telegram.reporting.i18n.I18nKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.repository.dto.SettingTO;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.SettingService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@EnableAsync
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final String START_PERIOD_NOTIFICATION_SETTING = "scheduling.notification.start.report.reminder";
    private final String FINISH_PERIOD_NOTIFICATION_SETTING = "scheduling.notification.finish.report.reminder";

    private final String EXCLUDE_EMPLOYEE_IDS = "scheduling.notification.exclude.employee.chat.ids";

    private final SettingService settingService;
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18nMessageService;

    @Scheduled(cron = "0 0 19 * * 4")
    public void beforeWeekendReportRemainder() {
        if (isSendingReportsRemainderEnable()) {
            log.info("Execute BeforeWeekendReportRemainder");
            executeReminderNotification(MessageKey.NOTIFICATION_REPORT_REMAINDER_BEFORE_WEEKEND);
        }
    }

    @Scheduled(cron = "0 0 11 * * 1")
    public void afterWeekendReportRemainder() {
        if (isSendingReportsRemainderEnable()) {
            log.info("Execute AfterWeekendReportRemainder");
            executeReminderNotification(MessageKey.NOTIFICATION_REPORT_REMAINDER_AFTER_WEEKEND);
        }
    }

    private void executeReminderNotification(I18nKey messageKey) {
        SettingTO excludeIdsSetting = settingService.getByKey(EXCLUDE_EMPLOYEE_IDS)
                .orElseThrow(() -> new NoSuchElementException("Setting not found by key - " + EXCLUDE_EMPLOYEE_IDS));

        List<Long> excludeChatIds = Arrays.stream(excludeIdsSetting.getValue().split(","))
                .mapToLong(Long::parseLong)
                .boxed()
                .toList();

        UserFilter filter = UserFilter.builder()
                .userStatus(UserFilter.UserStatus.ACTIVE)
                .build();

        StringBuilder listChatIdsLogging = new StringBuilder();
        List<User> employeesToRemind = userService.findUsers(filter).stream()
                .filter(user -> !excludeChatIds.contains(user.getChatId()))
                .peek(user -> listChatIdsLogging.append(user.getChatId()).append(" "))
                .toList();

        log.info("Notification sent to Chats = [{}]. Text [{}]", listChatIdsLogging, messageKey.value());

        employeesToRemind.forEach(user -> sendBotMessageService.sendMessage(user.getChatId(),
                i18nMessageService.getMessage(user.getChatId(), messageKey).formatted(user.getName())));
    }

    private boolean isSendingReportsRemainderEnable() {
        SettingTO startPeriodSetting = settingService.getByKey(START_PERIOD_NOTIFICATION_SETTING)
                .orElseThrow(() -> new NoSuchElementException("Setting not found by key - '%s'".formatted(START_PERIOD_NOTIFICATION_SETTING)));
        SettingTO finishPeriodSetting = settingService.getByKey(FINISH_PERIOD_NOTIFICATION_SETTING)
                .orElseThrow(() -> new NoSuchElementException("Setting not found by key - '%s'".formatted(FINISH_PERIOD_NOTIFICATION_SETTING)));

        LocalDate startReportRemainder = DateTimeUtils.parseShortDateToLocalDate(startPeriodSetting.getValue());
        LocalDate finishReportRemainder = DateTimeUtils.parseShortDateToLocalDate(finishPeriodSetting.getKey());

        return DateTimeUtils.isBelongToPeriod(LocalDate.now(), startReportRemainder, finishReportRemainder);
    }
}
