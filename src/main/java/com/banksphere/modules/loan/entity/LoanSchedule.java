package com.banksphere.modules.loan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private UUID loanId;
    private int installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    private LocalDate paidDate;
    private String status;
    
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;
}
