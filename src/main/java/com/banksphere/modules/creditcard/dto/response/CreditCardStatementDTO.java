package com.banksphere.modules.creditcard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCardStatementDTO {
    private UUID creditCardId;
    private String maskedCardNumber;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
    private BigDecimal totalAmount;
    private BigDecimal minimumAmount;
    private LocalDate dueDate;
    private String status;
    private List<CreditCardTransactionItem> transactions;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreditCardTransactionItem {
        private LocalDateTime date;
        private String merchantName;
        private BigDecimal amount;
        private String transactionType;
        private String referenceNumber;
    }
}
