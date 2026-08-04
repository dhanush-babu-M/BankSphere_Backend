package com.banksphere.modules.loan.repository;

import com.banksphere.modules.loan.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Page<Loan> findByCustomerId(UUID customerId, Pageable pageable);
    Optional<Loan> findByLoanId(String loanId);
    Page<Loan> findByStatus(String status, Pageable pageable);
    List<Loan> findByCustomerIdAndStatus(UUID customerId, String status);
    List<Loan> findByNextEmiDateAndStatus(LocalDate nextEmiDate, String status);
}
