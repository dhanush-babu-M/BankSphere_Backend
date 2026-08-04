package com.banksphere.modules.fixeddeposit.dto;

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
public class FixedDepositResponseDTO {
    private UUID id;
    private String fdNumber;
    private UUID customerId;
    private UUID accountId;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private String fdType;
    private String interestPayoutFrequency;
    private BigDecimal maturityAmount;
    private LocalDate maturityDate;
    private LocalDate startDate;
    private boolean autoRenew;
    private String status;
    private LocalDateTime createdAt;
}
