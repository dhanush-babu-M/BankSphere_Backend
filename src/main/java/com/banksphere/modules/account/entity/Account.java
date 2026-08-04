package com.banksphere.modules.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private String accountType;
    @Column(nullable = false)
    private UUID customerId;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private BigDecimal availableBalance;
    @Column(nullable = false)
    @Builder.Default
    private String currency = "INR";
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal overdraftLimit = BigDecimal.ZERO;
    private BigDecimal dailyTransactionLimit;
    private String branchCode;
    private String ifscCode;
    private BigDecimal interestRate;
    private LocalDateTime lastTransactionDate;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @CreatedBy
    private String createdBy;
    @Version
    private Long version;
}
