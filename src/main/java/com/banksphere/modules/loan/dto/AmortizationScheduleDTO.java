package com.banksphere.modules.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationScheduleDTO {
    private UUID loanId;
    private BigDecimal emiAmount;
    private BigDecimal totalAmount;
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private List<LoanScheduleItem> scheduleItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanScheduleItem {
        private int installmentNumber;
        private LocalDate dueDate;
        private BigDecimal principalAmount;
        private BigDecimal interestAmount;
        private BigDecimal totalAmount;
        private BigDecimal outstandingBalance;
    }
}
