package com.banksphere.modules.debitcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DebitCardLimitUpdateDTO {
    @NotNull
    private UUID cardId;

    private BigDecimal dailyAtmLimit;
    private BigDecimal dailyPosLimit;
    private BigDecimal dailyOnlineLimit;

    @NotBlank
    private String reason;
}
