package com.banksphere.modules.fixeddeposit.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class CreateFdRequestDTO {
    @NotNull
    private UUID customerId;
    
    @NotNull
    private UUID accountId;
    
    @NotNull
    @DecimalMin("1000.0")
    private BigDecimal principalAmount;
    
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;
    
    @NotNull
    @Min(1)
    private int tenureMonths;
    
    @NotBlank
    private String fdType;
    
    @NotBlank
    private String interestPayoutFrequency;
    
    private boolean autoRenew;
    private String nomineeName;
    private String nomineeRelation;
}
