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
public class EmiPaymentRequestDTO {
    @NotNull
    private UUID loanId;
    
    @NotNull
    private BigDecimal paymentAmount;
    
    @NotNull
    private UUID paymentAccountId;
    
    private String paymentMethod;
}
