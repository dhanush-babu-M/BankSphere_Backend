package com.banksphere.modules.loan.repository;

import com.banksphere.modules.loan.entity.LoanApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    Page<LoanApplication> findByCustomerId(UUID customerId, Pageable pageable);
    Optional<LoanApplication> findByApplicationNumber(String applicationNumber);
    Page<LoanApplication> findByStatus(String status, Pageable pageable);
}
