package com.banksphere.modules.loan.service;

import com.banksphere.modules.loan.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoanService {
    LoanResponseDTO applyForLoan(LoanApplicationRequestDTO dto);
    LoanResponseDTO getLoanApplication(UUID id);
    LoanResponseDTO approveLoan(LoanApprovalDTO dto, String approvedBy);
    LoanResponseDTO disburseLoan(UUID loanId, String disbursedBy);
    Page<LoanResponseDTO> getCustomerLoans(UUID customerId, Pageable pageable);
    LoanResponseDTO getLoan(UUID id);
    EmiRepaymentResponseDTO payEmi(EmiPaymentRequestDTO dto);
    AmortizationScheduleDTO getAmortizationSchedule(UUID loanId);
    AmortizationScheduleDTO generateAmortizationSchedule(BigDecimal principal, BigDecimal rate, int months);
    BigDecimal calculateEmi(BigDecimal principal, BigDecimal rate, int months);
    void closeLoan(UUID loanId);
    Page<LoanResponseDTO> getLoansByStatus(String status, Pageable pageable);
}
