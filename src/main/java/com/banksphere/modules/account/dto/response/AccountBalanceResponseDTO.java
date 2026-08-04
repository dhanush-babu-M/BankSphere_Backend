package com.banksphere.modules.account.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponseDTO {
    private UUID accountId;
    private String accountNumber;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal holdAmount;
    private String currency;
    private LocalDateTime asOfDateTime;
}
