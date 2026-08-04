package com.banksphere.modules.debitcard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DebitCardResponseDTO {
    private UUID id;
    private UUID accountId;
    private UUID customerId;
    private String maskedCardNumber;
    private String cardHolderName;
    private String cardType;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal dailyAtmLimit;
    private BigDecimal dailyPosLimit;
    private BigDecimal dailyOnlineLimit;
    private boolean contactlessEnabled;
    private boolean internationalEnabled;
    private LocalDateTime issuedAt;
}
