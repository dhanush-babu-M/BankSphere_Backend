package com.banksphere.modules.creditcard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCardResponseDTO {
    private UUID id;
    private UUID customerId;
    private String maskedCardNumber;
    private String cardHolderName;
    private String cardType;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
    private BigDecimal outstandingBalance;
    private BigDecimal minimumPayment;
    private LocalDate dueDate;
    private LocalDate expiryDate;
    private String status;
    private int rewardPoints;
    private BigDecimal dailyLimit;
    private BigDecimal perTransactionLimit;
    private boolean internationalTransactionEnabled;
}
