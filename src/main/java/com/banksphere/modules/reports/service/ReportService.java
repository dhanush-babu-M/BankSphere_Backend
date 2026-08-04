package com.banksphere.modules.reports.service;

import com.banksphere.modules.reports.dto.AuditReportDTO;
import com.banksphere.modules.reports.dto.FinancialSummaryReportDTO;
import com.banksphere.modules.reports.dto.StatementRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReportService {
    byte[] generateAccountStatement(StatementRequestDTO request);
    List<Map<String, Object>> generateAccountStatementJson(StatementRequestDTO request);
    FinancialSummaryReportDTO generateFinancialSummary(UUID customerId, LocalDate from, LocalDate to);
    AuditReportDTO generateAuditReport(String module, LocalDate from, LocalDate to, String generatedBy);
    Page<Map<String, Object>> generateTransactionReport(LocalDate from, LocalDate to, String type, Pageable pageable);
    Map<String, Object> generateLoanPortfolioReport();
    Map<String, Object> generateDailyTransactionSummary(LocalDate date);
}
