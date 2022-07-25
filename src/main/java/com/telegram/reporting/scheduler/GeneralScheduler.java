package com.telegram.reporting.scheduler;

import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableAsync
@Component
@PropertySource(value = "classpath:scheduler-props.yml", factory = YamlPropertySourceFactory.class)
public class GeneralScheduler {

    private final TelegramUserService userService;
    private final LockUpdateReportService lockService;
    private final Environment env;

    public GeneralScheduler(TelegramUserService userService, LockUpdateReportService lockService,
                            Environment env) {
        this.userService = userService;
        this.lockService = lockService;
        this.env = env;
    }

    @Scheduled(cron = "${lockEditReport.cron}")
    public void lockEditReport() {
        log.info("LockEditReport job started");
        Integer offsetMinusMonth = env.getProperty("lockEditReport.offset.minusMonth", Integer.class);
        Validate.notNull(offsetMinusMonth, "Offset of month from scheduler-props file is required for locking reports to update!");

        LocalDate statisticMonth = LocalDate.now().minusMonths(offsetMinusMonth);
        UserFilter filter = UserFilter.builder()
                .userStatus(UserFilter.UserStatus.ACTIVE, UserFilter.UserStatus.DELETED)
                .build();
        List<User> users = userService.findUsers(filter);
        List<Long> notLockedIds = users.stream()
                .map(User::getId)
                .filter(id -> !lockService.lockExist(id, statisticMonth))
                .toList();

        notLockedIds.forEach(id -> lockService.saveLock(id, statisticMonth));

        log.info("LockEditReport job was finished successfully. Employee with reports = %d. Didn't lock = %d"
                .formatted(users.size(), notLockedIds.size()));
    }

    @Scheduled(cron = "${removeNotAuthorizedUsers.cron}")
    public void removeNotAuthorizedUsers() {
        log.info("RemoveNotAuthorizedUsers job started");
        Integer offsetMinusDays = env.getProperty("removeNotAuthorizedUsers.offset.minusDays", Integer.class);
        Validate.notNull(offsetMinusDays, "Offset of days from scheduler-props file is required for remove not authorized users!");

        LocalDate offsetRemoveDate = LocalDate.now().minusDays(offsetMinusDays);
        UserFilter filter = UserFilter.builder()
                .userStatus(UserFilter.UserStatus.ACTIVE_NOT_VERIFIED, UserFilter.UserStatus.DELETED_NOT_VERIFIED)
                .build();
        List<User> users = userService.findUsers(filter);

        List<User> toRemove = users.stream()
                .filter(user -> offsetRemoveDate.isAfter(user.getCreated().toLocalDate()))
                .toList();

        String phones = toRemove.stream()
                .map(User::getPhone)
                .collect(Collectors.joining(", "));

        toRemove.forEach(userService::removeNotAuthorizedUsers);

        log.info("RemoveNotAuthorizedUsers job was finished successfully. Deleted not authorized users with phone numbers = [%s]"
                .formatted(phones));

    }
}
