package com.banksphere.modules.fixeddeposit.repository;

import com.banksphere.modules.fixeddeposit.entity.FixedDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, UUID> {
    List<FixedDeposit> findByCustomerId(UUID customerId);
    Page<FixedDeposit> findByCustomerId(UUID customerId, Pageable pageable);
    Optional<FixedDeposit> findByFdNumber(String fdNumber);
    List<FixedDeposit> findByStatus(String status);
    List<FixedDeposit> findByMaturityDateAndStatus(LocalDate maturityDate, String status);
    List<FixedDeposit> findByMaturityDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
}
