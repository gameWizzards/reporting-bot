package com.telegram.reporting.scheduler;

import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.SettingService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableAsync
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final SettingService settingService;
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;

    @Scheduled(cron = "0 0 19 * * 4")
    public void endWeekReportRemainder() {
        var isEnable = Boolean.parseBoolean(
                settingService.getValue("scheduling.notification.end.week.remainder.enable")
                        .orElse("false"));
        if (isEnable) {
            log.info("Execute EndWeekReportRemainder");
            var message = "%s, впереди выходные и работа на заказах, отправь отчеты за работу в будние дни)";
            executeReminderNotification(message);
        }
    }

    @Scheduled(cron = "0 0 11 * * 1")
    public void afterWeekendReportRemainder() {
        var isEnable = Boolean.parseBoolean(
                settingService.getValue("scheduling.notification.after.weekend.remainder.enable")
                        .orElse("false"));
        if (isEnable) {
            log.info("Execute AfterWeekendReportRemainder");
            var message = "%s, выходные прошли, не забудь отправить отчеты)";
            executeReminderNotification(message);
        }
    }

    private void executeReminderNotification(String message) {
        StringBuilder listChatIdsLogging = new StringBuilder();
        String excludeEmployeeByChatIds = settingService.getValue("scheduling.notification.exclude.employee.chat.ids").orElse("");
        List<Long> excludeChatIds = Arrays.stream(excludeEmployeeByChatIds.split(","))
                .mapToLong(Long::parseLong)
                .boxed()
                .toList();

        UserFilter filter = UserFilter.builder()
                .userStatus(UserFilter.UserStatus.ACTIVE)
                .build();

        List<User> employeesToRemindChatIds = userService.findUsers(filter).stream()
                .filter(user -> !excludeChatIds.contains(user.getChatId()))
                .peek(user -> listChatIdsLogging.append(user.getChatId()).append(" "))
                .toList();

        log.info("Notification sent to Chats = [{}]. Text [{}]", listChatIdsLogging, message);
        employeesToRemindChatIds.forEach(user -> sendBotMessageService.sendMessage(user.getChatId(), message.formatted(user.getName())));
    }
}
