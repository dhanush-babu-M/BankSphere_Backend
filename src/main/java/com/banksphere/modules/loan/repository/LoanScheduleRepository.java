package com.banksphere.modules.loan.repository;

import com.banksphere.modules.loan.entity.LoanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoanScheduleRepository extends JpaRepository<LoanSchedule, UUID> {
    List<LoanSchedule> findByLoanId(UUID loanId);
    List<LoanSchedule> findByLoanIdAndStatus(UUID loanId, String status);
    List<LoanSchedule> findByLoanIdOrderByInstallmentNumber(UUID loanId);
    List<LoanSchedule> findByDueDateAndStatus(LocalDate dueDate, String status);
}
