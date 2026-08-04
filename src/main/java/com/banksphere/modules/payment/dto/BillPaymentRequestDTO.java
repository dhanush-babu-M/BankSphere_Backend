package com.banksphere.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentRequestDTO {

    @NotBlank
    private String payerAccountNumber;

    @NotBlank
    private String merchantCode;

    @NotBlank
    private String billNumber;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal amount;

    private String customerIdentifier; // mobile/consumer number

    private String remarks;
}
