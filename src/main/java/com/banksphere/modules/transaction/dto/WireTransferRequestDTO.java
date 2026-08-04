package com.banksphere.modules.transaction.dto;

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
public class WireTransferRequestDTO {

    @NotBlank
    private String sourceAccountNumber;

    @NotBlank
    private String swiftCode;

    @NotBlank
    private String destinationBankName;

    @NotBlank
    private String destinationAccountNumber;

    @NotBlank
    private String beneficiaryName;

    private String beneficiaryAddress;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String currency;

    private String correspondentBankDetails;

    @NotBlank
    private String purpose;

    private String remarks;
}
