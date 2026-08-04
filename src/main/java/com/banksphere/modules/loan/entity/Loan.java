package com.banksphere.modules.loan.entity;

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
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String loanId;
    
    private UUID applicationId;
    private UUID customerId;
    private UUID disbursementAccountId;
    private String loanType;
    private BigDecimal sanctionedAmount;
    private BigDecimal disbursedAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal processingFee;
    private BigDecimal outstandingPrincipal;
    private BigDecimal outstandingInterest;
    private LocalDate nextEmiDate;
    private LocalDate lastEmiDate;
    private int totalEmisCount;
    private int emisPaid;
    private int emisRemaining;
    private String status;
    private LocalDateTime disbursedAt;
    private LocalDateTime closedAt;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
