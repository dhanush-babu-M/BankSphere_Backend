package com.banksphere.modules.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String applicationNumber;
    
    private UUID customerId;
    private String loanType;
    private BigDecimal requestedAmount;
    private int requestedTenureMonths;
    private String purpose;
    private BigDecimal annualIncome;
    private BigDecimal existingEmi;
    private int creditScore;
    private String status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private String rejectionReason;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
