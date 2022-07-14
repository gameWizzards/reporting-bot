package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.phone=?1")
    User findByPhone(String phone);

    @Query(value = "SELECT u FROM User u WHERE u.chatId=?1")
    User findByChatId(Long chatId);

    @Query(value = "SELECT u FROM User u WHERE u.deleted=false AND u.chatId IS NULL")
    List<User> findAllNotActive();

    @Query(value = "SELECT u FROM User u WHERE (:name is null or u.name=:name) AND (:surname is null or u.surname=:surname)" +
            " AND (:deleted is null or u.deleted=:deleted) AND u.chatId IS NOT NULL")
    List<User> findUsers(@Param("name") String name, @Param("surname") String surname, @Param("deleted") Boolean deleted);
}