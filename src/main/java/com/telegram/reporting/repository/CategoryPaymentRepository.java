package com.telegram.reporting.repository;

import com.telegram.reporting.domain.CategoryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPaymentRepository extends JpaRepository<CategoryPayment, Long> {
}
