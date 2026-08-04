package com.banksphere.modules.fixeddeposit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FdInterestCalculationDTO {
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private String fdType;
    private BigDecimal maturityAmount;
    private BigDecimal totalInterest;
    private BigDecimal effectiveAnnualReturn;
    private List<MonthlyBreakup> breakup;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyBreakup {
        private int month;
        private BigDecimal openingBalance;
        private BigDecimal interest;
        private BigDecimal closingBalance;
    }
}
