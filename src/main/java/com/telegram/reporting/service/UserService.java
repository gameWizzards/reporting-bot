package com.telegram.reporting.service;

import com.telegram.reporting.bot.event.SendContactEvent;
import com.telegram.reporting.dto.EmployeeTO;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.repository.filter.UserFilter;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    User save(User user);

    User findById(Long id);

    User findByChatId(Long chatId);

    List<User> findUsers(UserFilter filter);

    User verifyContact(SendContactEvent event);

    List<EmployeeTO> findEmployeesWithExistReportsByMonth(LocalDate statisticMonth);

    User findByPhone(String phone);

    void removeNotAuthorizedUsers(User user);
}
