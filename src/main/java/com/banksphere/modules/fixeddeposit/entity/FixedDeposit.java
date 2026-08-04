package com.banksphere.modules.fixeddeposit.entity;

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
@Table(name = "fixed_deposits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FixedDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String fdNumber;

    private UUID customerId;
    private UUID accountId;
    
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private int tenureDays;
    
    private String fdType;
    private String interestPayoutFrequency;
    
    private BigDecimal maturityAmount;
    private LocalDate maturityDate;
    private LocalDate startDate;
    
    @Builder.Default
    private boolean autoRenew = false;
    
    private String nomineeName;
    private String nomineeRelation;
    private String status;
    private BigDecimal prematureClosurePenalty;
    private BigDecimal actualMaturityAmount;
    
    private LocalDateTime closedAt;
    private String closureReason;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
