package com.telegram.reporting.scheduler;

import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@EnableAsync
@Component
@PropertySource(value = "classpath:scheduler-props.yml", factory = YamlPropertySourceFactory.class)
public class GeneralScheduler {
    @Value("${lockEditReport.offset.minusMonth}")
    private int offsetMinusMonth;

    private final TelegramUserService userService;
    private final LockUpdateReportService lockService;

    public GeneralScheduler(TelegramUserService userService, LockUpdateReportService lockService) {
        this.userService = userService;
        this.lockService = lockService;
    }

    @Scheduled(cron = "${lockEditReport.cron}")
    public void lockEditReport() {
        log.info("LockEditReport job started");
        LocalDate statisticMonth = LocalDate.now().minusMonths(offsetMinusMonth);
        List<EmployeeTO> employeeTOs = userService.findEmployeesWithExistReportsByMonth(statisticMonth);
        List<Long> notLockedIds = employeeTOs.stream()
                .map(EmployeeTO::getId)
                .filter(id -> !lockService.lockExist(id, statisticMonth))
                .toList();

        notLockedIds.forEach(id -> lockService.saveLock(id, statisticMonth));

        log.info("LockEditReport job was finished successfully. Employee with reports = %d. Didn't lock = %d"
                .formatted(employeeTOs.size(), notLockedIds.size()));
    }
}
