package com.banksphere.modules.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private UUID id;
    private String loanId;
    private UUID applicationId;
    private UUID customerId;
    private String loanType;
    private BigDecimal sanctionedAmount;
    private BigDecimal disbursedAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal outstandingPrincipal;
    private BigDecimal outstandingInterest;
    private LocalDate nextEmiDate;
    private int emisPaid;
    private int emisRemaining;
    private String status;
    private LocalDateTime disbursedAt;
}
