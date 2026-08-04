package com.banksphere.modules.creditcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_card_bills")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCardBill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "credit_card_id")
    private UUID creditCardId;

    @Column(name = "billing_period_start")
    private LocalDate billingPeriodStart;

    @Column(name = "billing_period_end")
    private LocalDate billingPeriodEnd;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "minimum_amount")
    private BigDecimal minimumAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "status")
    private String status;

    @CreatedDate
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
}
