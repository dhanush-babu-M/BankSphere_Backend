package com.banksphere.modules.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class ExternalTransferRequestDTO {

    @NotBlank
    private String sourceAccountNumber;

    private UUID beneficiaryId; // nullable

    @NotBlank
    private String destinationAccountNumber;

    @NotBlank
    private String destinationIfsc;

    @NotBlank
    private String beneficiaryName;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal amount;

    @NotBlank
    private String paymentMode; // NEFT/RTGS/IMPS

    private String narration;

    private String remarks;
}
