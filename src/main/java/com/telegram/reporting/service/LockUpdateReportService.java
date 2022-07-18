package com.telegram.reporting.service;

import java.time.LocalDate;

public interface LockUpdateReportService {

    void saveLock(Long userId, LocalDate lockMonth);

    void deleteLock(Long userId, LocalDate lockMonth);

    boolean lockExist(Long userId, LocalDate lockMonth);
}
