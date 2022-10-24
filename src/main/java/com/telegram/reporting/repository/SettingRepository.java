package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    @Query(value = "SELECT s.value FROM Setting s WHERE s.key=?1")
    String getValue(String key);
}
