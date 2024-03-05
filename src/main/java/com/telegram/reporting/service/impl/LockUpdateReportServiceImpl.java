package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.LockUpdateReportRepository;
import com.telegram.reporting.domain.LockUpdateReport;
import com.telegram.reporting.service.LockUpdateReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
public class LockUpdateReportServiceImpl implements LockUpdateReportService {
    private final int DEFAULT_DAY = 1;
    private final LockUpdateReportRepository lockRepository;

    public LockUpdateReportServiceImpl(LockUpdateReportRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    @Override
    public void saveLock(Long userId, LocalDate lockMonth) {
        Validate.notNull(userId, "UserId is required to save LockUpdateReport");
        Validate.notNull(lockMonth, "LockMonth is required to save LockUpdateReport");
        lockRepository.save(new LockUpdateReport(userId, LocalDate.of(lockMonth.getYear(), lockMonth.getMonthValue(), DEFAULT_DAY)));
    }

    @Transactional
    @Override
    public void deleteLock(Long userId, LocalDate lockMonth) {
        Validate.notNull(userId, "UserId is required to delete LockUpdateReport");
        Validate.notNull(lockMonth, "LockMonth is required to delete LockUpdateReport");
        lockRepository.delete(userId, lockMonth.getMonthValue(), lockMonth.getYear());
    }

    @Override
    public boolean lockExist(Long userId, LocalDate lockMonth) {
        Validate.notNull(userId, "UserId is required to check existing of LockUpdateReport");
        Validate.notNull(lockMonth, "LockMonth is required to check existing of LockUpdateReport");
        return lockRepository.lockExist(userId, lockMonth.getMonthValue(), lockMonth.getYear());
    }
}
