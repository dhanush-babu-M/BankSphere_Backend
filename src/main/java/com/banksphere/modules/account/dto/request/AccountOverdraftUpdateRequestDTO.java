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
public class AccountOverdraftUpdateRequestDTO {
    @NotNull
    private UUID accountId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal overdraftLimit;
    @NotBlank
    private String reason;
}
