package com.banksphere.modules.loan.repository;

import com.banksphere.modules.loan.entity.LoanCollateral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanCollateralRepository extends JpaRepository<LoanCollateral, UUID> {
    List<LoanCollateral> findByLoanId(UUID loanId);
    List<LoanCollateral> findByLoanIdAndVerified(UUID loanId, boolean verified);
}
