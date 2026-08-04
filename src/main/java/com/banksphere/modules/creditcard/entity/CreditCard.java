package com.banksphere.modules.creditcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_cards")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(name = "masked_card_number")
    private String maskedCardNumber;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

    @Column(name = "available_credit")
    private BigDecimal availableCredit;

    @Column(name = "outstanding_balance")
    private BigDecimal outstandingBalance;

    @Column(name = "minimum_payment")
    private BigDecimal minimumPayment;

    @Column(name = "billing_cycle_day")
    private int billingCycleDay;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "status")
    private String status;

    @Column(name = "international_transaction_enabled")
    private boolean internationalTransactionEnabled;

    @Column(name = "contactless_enabled")
    private boolean contactlessEnabled;

    @Column(name = "daily_limit")
    private BigDecimal dailyLimit;

    @Column(name = "per_transaction_limit")
    private BigDecimal perTransactionLimit;

    @Column(name = "reward_points")
    private int rewardPoints;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
