package com.banksphere.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalPaymentRequestDTO {

    @NotBlank
    private String sourceAccountNumber;

    @NotBlank
    private String destinationAccountNumber;

    @NotBlank
    private String destinationBankIfsc;

    @NotBlank
    private String beneficiaryName;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal amount;

    @NotBlank
    private String paymentType; // NEFT/RTGS/IMPS

    private String narration;

    private LocalDateTime scheduledAt;
}
