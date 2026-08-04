package com.banksphere.modules.transaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String referenceNumber; // unique - TXN+YYYYMMdd+8random

    private UUID accountId;

    private UUID linkedAccountId; // nullable

    private String transactionType;

    private BigDecimal amount;

    @Builder.Default
    private String currency = "INR";

    private String description;

    private String narration;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String status;

    private String channel; // NET_BANKING/MOBILE/ATM/BRANCH/API/SCHEDULER

    private String ipAddress;

    private String deviceInfo;

    private String beneficiaryName;

    private String beneficiaryAccountNumber; // masked

    private String ifscCode;

    private String paymentMode; // NEFT/RTGS/IMPS/UPI/INTERNAL

    private LocalDate valueDate;

    private LocalDate settlementDate;

    private String reversalReason;

    private LocalDateTime reversedAt;

    private String initiatedBy;

    private String approvedBy;

    private String gatewayReference;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
