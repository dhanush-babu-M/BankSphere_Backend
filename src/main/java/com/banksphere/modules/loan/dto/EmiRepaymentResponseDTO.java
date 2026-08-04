package com.banksphere.modules.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmiRepaymentResponseDTO {
    private UUID loanId;
    private int installmentNumber;
    private BigDecimal paidAmount;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal penaltyWaived;
    private BigDecimal outstandingAfterPayment;
    private LocalDate nextEmiDate;
    private String transactionReference;
}
