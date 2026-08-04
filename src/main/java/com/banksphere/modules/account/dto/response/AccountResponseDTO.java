package com.banksphere.modules.account.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private UUID id;
    private String accountNumber;
    private String accountType;
    private UUID customerId;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
    private String status;
    private BigDecimal overdraftLimit;
    private BigDecimal dailyTransactionLimit;
    private String branchCode;
    private String ifscCode;
    private BigDecimal interestRate;
    private LocalDateTime lastTransactionDate;
    private LocalDateTime createdAt;
}
