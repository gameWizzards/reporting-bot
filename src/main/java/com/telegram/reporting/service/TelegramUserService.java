package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TelegramUserService {

    User save(User user);

    Optional<User> findByChatId(Long chatId);

    List<User> findAll();

    List<User> findUsers(UserFilter filter);

    User verifyContact(Message message);

    List<EmployeeTO> findEmployeesWithExistReportsByMonth(LocalDate statisticMonth);

    Optional<User> findByPhone(String phone);
}
