package com.banksphere.modules.debitcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueDebitCardRequestDTO {
    @NotNull
    private UUID accountId;

    @NotNull
    private UUID customerId;

    @NotBlank
    private String cardHolderName;

    @NotBlank
    private String cardType;

    private BigDecimal dailyAtmLimit;
    private BigDecimal dailyPosLimit;
    private BigDecimal dailyOnlineLimit;
}
