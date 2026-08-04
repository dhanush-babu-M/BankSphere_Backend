package com.banksphere.modules.loan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApprovalDTO {
    @NotNull
    private UUID applicationId;
    
    @NotNull
    private boolean approved;
    
    private BigDecimal sanctionedAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private UUID disbursementAccountId;
    private String reviewNotes;
    private String rejectionReason;
}
