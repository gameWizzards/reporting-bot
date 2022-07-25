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

    @Query(value = "SELECT u FROM User u WHERE u.deleted=false AND u.activated IS NULL")
    List<User> findActiveNotVerifiedUsers();

    @Query(value = """
            SELECT u FROM User u WHERE (:name is null or u.name=:name) AND (:surname is null or u.surname=:surname)
            AND u.deleted=false AND u.activated IS NOT NULL""")
    List<User> findActiveUsers(@Param("name") String name, @Param("surname") String surname);

    @Query(value = """
            SELECT u FROM User u WHERE (:name is null or u.name=:name) AND (:surname is null or u.surname=:surname)
            AND u.deleted=true""")
    List<User> findDeletedUsers(@Param("name") String name, @Param("surname") String surname);

    @Query(value = """
            SELECT u FROM User u LEFT JOIN u.reports r WHERE u.chatId IS NOT NULL
            AND extract(month from r.date)=:month AND extract(year from r.date)=:year
            GROUP BY u.id ORDER BY u.name""")
    List<User> findUsersWithExistReportsByMonth(@Param("month") int month, @Param("year") int year);
}