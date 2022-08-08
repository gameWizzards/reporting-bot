package com.telegram.reporting.scheduler;

import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@EnableAsync
@Component
@PropertySource(value = "classpath:scheduler-props.yml", factory = YamlPropertySourceFactory.class)
public class NotificationScheduler {

    private final Environment env;
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;

    public NotificationScheduler(Environment env, TelegramUserService userService,
                                 SendBotMessageService sendBotMessageService) {
        this.env = env;
        this.userService = userService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Scheduled(cron = "${reportRemainder.endWeek.cron}")
    public void endWeekReportRemainder() {
        log.info("Execute EndWeekReportRemainder");
        String message = Optional.ofNullable(env.getProperty("reportRemainder.endWeek.notification")).orElse("Empty notification (((");
        executeReminderNotification(message);
    }

    @Scheduled(cron = "${reportRemainder.afterWeekend.cron}")
    public void afterWeekendReportRemainder() {
        log.info("Execute AfterWeekendReportRemainder");
        String message = Optional.ofNullable(env.getProperty("reportRemainder.afterWeekend.notification")).orElse("Empty notification (((");
        executeReminderNotification(message);
    }

  
    private void executeReminderNotification(String message) {
        StringBuilder listChatIdsLogging = new StringBuilder();
        String excludeEmployeeByChatIds = Optional.ofNullable(env.getProperty("reportRemainder.excludeEmployeeByChatId")).orElse("Empty notification (((");
        List<Long> excludeChatIds = Arrays.stream(excludeEmployeeByChatIds.split(",")).mapToLong(Long::parseLong).boxed().toList();
        
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
