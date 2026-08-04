package com.banksphere.modules.reports.dto;

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
public class FinancialSummaryReportDTO {
    private UUID customerId;
    private String customerName;
    private LocalDate reportPeriodStart;
    private LocalDate reportPeriodEnd;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal netFlow;
    private int transactionCount;
    private BigDecimal averageTransactionAmount;
    private List<AccountSummaryItem> accounts;
    private List<CategorySummary> topCategories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummaryItem {
        private UUID accountId;
        private String accountNumber;
        private BigDecimal balance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private String category;
        private BigDecimal totalAmount;
    }
}
