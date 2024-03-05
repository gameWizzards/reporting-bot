package com.telegram.reporting.repository;

import com.telegram.reporting.domain.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

}
