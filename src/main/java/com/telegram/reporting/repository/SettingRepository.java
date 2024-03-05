package com.telegram.reporting.repository;

import com.telegram.reporting.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    @Query(value = "SELECT s FROM Setting s WHERE s.key=?1")
    Optional<Setting> getByKey(String key);
}
