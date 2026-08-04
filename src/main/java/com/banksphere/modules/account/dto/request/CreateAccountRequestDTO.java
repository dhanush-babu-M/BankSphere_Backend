package com.banksphere.modules.account.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequestDTO {
    @NotNull
    private UUID customerId;
    @NotBlank
    private String accountType;
    @Builder.Default
    private String currency = "INR";
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal initialDeposit;
    @NotBlank
    private String branchCode;
    private String nomineeName;
    private String nomineeRelation;
}
