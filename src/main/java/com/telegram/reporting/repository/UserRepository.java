package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Repository} for handling with {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.phone=?1")
    User findByPhone(String phone);

    @Query(value = "SELECT u FROM User u WHERE u.chatId=?1")
    User findByChatId(Long chatId);
}
