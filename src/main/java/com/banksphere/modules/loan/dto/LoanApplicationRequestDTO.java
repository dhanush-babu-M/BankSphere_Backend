package com.banksphere.modules.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class LoanApplicationRequestDTO {
    @NotNull
    private UUID customerId;
    
    @NotBlank
    private String loanType;
    
    @NotNull
    @DecimalMin("10000.0")
    private BigDecimal requestedAmount;
    
    @NotNull
    @Min(6)
    @Max(360)
    private int requestedTenureMonths;
    
    @NotBlank
    private String purpose;
    
    private BigDecimal annualIncome;
    private BigDecimal existingEmi;
}
