package com.telegram.reporting.scheduler;

import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.SettingService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@EnableAsync
@Component
@RequiredArgsConstructor
public class GeneralScheduler {

    private final String LOCK_EDIT_REPORT_SETTING_KEY = "scheduling.general.lock.edit.report.offset.month";
    private final String REMOVE_UNAUTHORIZED_USER_SETTING_KEY = "scheduling.general.remove.unauthorized.user.offset.days";

    private final TelegramUserService userService;
    private final LockUpdateReportService lockService;
    private final SettingService settingService;

    @Scheduled(cron = "0 0 2 1,11 * *")
    public void lockEditReport() {
        log.info("LockEditReport job started");
        String offsetSetting = settingService.getValue(LOCK_EDIT_REPORT_SETTING_KEY).orElse("");

        validateSettingValue(LOCK_EDIT_REPORT_SETTING_KEY, offsetSetting);

        int offsetMinusMonth = Integer.parseInt(offsetSetting);

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

    @Scheduled(cron = "0 0 1 * * *")
    public void removeNotAuthorizedUsers() {
        log.info("RemoveNotAuthorizedUsers job started");
        String offsetSetting = settingService.getValue(REMOVE_UNAUTHORIZED_USER_SETTING_KEY).orElse("");

        validateSettingValue(REMOVE_UNAUTHORIZED_USER_SETTING_KEY, offsetSetting);
        int offsetMinusDays = Integer.parseInt(offsetSetting);

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

    private void validateSettingValue(String key, String value) {
        if (StringUtils.isBlank(value)) {
            throw new NoSuchElementException("Value doesn't exist or empty for key: %s ".formatted(key));
        }
    }
}
