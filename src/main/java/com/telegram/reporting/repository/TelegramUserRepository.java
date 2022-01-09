package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link Repository} for handling with {@link User} entity.
 */
@Repository
public interface TelegramUserRepository extends JpaRepository<User, Long> {
}
