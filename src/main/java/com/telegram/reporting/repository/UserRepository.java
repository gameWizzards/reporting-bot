package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Repository} for handling with {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phoneNumber);

    User findByChatId(Long chatId);
}
