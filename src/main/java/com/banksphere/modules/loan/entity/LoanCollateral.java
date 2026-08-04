package com.banksphere.modules.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_collaterals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LoanCollateral {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private UUID loanId;
    private String collateralType;
    private String description;
    private BigDecimal estimatedValue;
    private String documentPath;
    
    @Builder.Default
    private boolean verified = false;
    
    private String verifiedBy;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
