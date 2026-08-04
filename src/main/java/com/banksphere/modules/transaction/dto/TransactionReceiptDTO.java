package com.banksphere.modules.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReceiptDTO {
    private String referenceNumber;
    private LocalDateTime transactionDate;
    private String senderName;
    private String senderAccountNumber;
    private String receiverName;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String currency;
    private String paymentMode;
    private String status;
    private String narration;
    private BigDecimal charges;
    private BigDecimal netAmount;
}
