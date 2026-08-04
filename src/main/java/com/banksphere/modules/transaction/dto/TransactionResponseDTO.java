package com.banksphere.modules.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private UUID id;
    private String referenceNumber;
    private UUID accountId;
    private String transactionType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String status;
    private String channel;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String paymentMode;
    private LocalDate valueDate;
    private LocalDateTime createdAt;
}
