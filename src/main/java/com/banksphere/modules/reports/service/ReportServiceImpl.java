package com.banksphere.modules.reports.service;

import com.banksphere.modules.reports.dto.StatementRequestDTO;
import com.banksphere.modules.reports.dto.FinancialSummaryReportDTO;
import com.banksphere.modules.reports.dto.AuditReportDTO;
import com.banksphere.modules.reports.mapper.ReportMapper;
import com.banksphere.core.utility.PDFGeneratorUtil;
import com.banksphere.modules.transaction.repository.TransactionRepository;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.modules.transaction.entity.Transaction;
import com.banksphere.core.audit.repository.AuditLogRepository;
import com.banksphere.core.audit.entity.AuditLog;
import com.banksphere.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private final PDFGeneratorUtil pdfGeneratorUtil;
    private final ReportMapper reportMapper;

    @Override
    public byte[] generateAccountStatement(StatementRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId().toString()));
                
        List<Transaction> transactions = transactionRepository.findByAccountIdAndCreatedAtBetween(
                request.getAccountId(), 
                request.getFromDate().atStartOfDay(), 
                request.getToDate().atTime(23, 59, 59), 
                Pageable.unpaged()
        ).getContent();
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("account", account);
        templateData.put("transactions", transactions);
        templateData.put("periodFrom", request.getFromDate());
        templateData.put("periodTo", request.getToDate());
        
        return pdfGeneratorUtil.generateAccountStatement(templateData);
    }

    @Override
    public List<Map<String, Object>> generateAccountStatementJson(StatementRequestDTO request) {
        List<Transaction> transactions = transactionRepository.findByAccountIdAndCreatedAtBetween(
                request.getAccountId(), 
                request.getFromDate().atStartOfDay(), 
                request.getToDate().atTime(23, 59, 59), 
                Pageable.unpaged()
        ).getContent();
        
        return transactions.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("referenceNumber", t.getReferenceNumber());
            map.put("transactionType", t.getTransactionType());
            map.put("amount", t.getAmount());
            map.put("balanceBefore", t.getBalanceBefore());
            map.put("balanceAfter", t.getBalanceAfter());
            map.put("status", t.getStatus());
            map.put("createdAt", t.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public FinancialSummaryReportDTO generateFinancialSummary(UUID customerId, LocalDate from, LocalDate to) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId, Pageable.unpaged()).getContent();
        
        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalDebits = BigDecimal.ZERO;
        int transactionCount = 0;
        
        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository.findByAccountIdAndCreatedAtBetween(
                    account.getId(),
                    from.atStartOfDay(),
                    to.atTime(23, 59, 59),
                    Pageable.unpaged()
            ).getContent();
            
            for (Transaction t : transactions) {
                if ("CREDIT".equals(t.getTransactionType())) {
                    totalCredits = totalCredits.add(t.getAmount());
                } else if ("DEBIT".equals(t.getTransactionType())) {
                    totalDebits = totalDebits.add(t.getAmount());
                }
                transactionCount++;
            }
        }
        
        FinancialSummaryReportDTO report = new FinancialSummaryReportDTO();
        report.setCustomerId(customerId);
        report.setReportPeriodStart(from);
        report.setReportPeriodEnd(to);
        report.setTotalCredits(totalCredits);
        report.setTotalDebits(totalDebits);
        report.setNetFlow(totalCredits.subtract(totalDebits));
        report.setTransactionCount(transactionCount);
        if (transactionCount > 0) {
            report.setAverageTransactionAmount(totalCredits.add(totalDebits).divide(BigDecimal.valueOf(transactionCount), 2, java.math.RoundingMode.HALF_UP));
        } else {
            report.setAverageTransactionAmount(BigDecimal.ZERO);
        }
        report.setAccounts(accounts.stream().map(acc -> {
            FinancialSummaryReportDTO.AccountSummaryItem item = new FinancialSummaryReportDTO.AccountSummaryItem();
            item.setAccountId(acc.getId());
            item.setAccountNumber(acc.getAccountNumber());
            item.setBalance(acc.getBalance());
            return item;
        }).collect(Collectors.toList()));
        
        return report;
    }

    @Override
    public AuditReportDTO generateAuditReport(String module, LocalDate from, LocalDate to, String generatedBy) {
        List<AuditLog> logs = auditLogRepository.findByModuleAndCreatedAtBetween(
                module,
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                Pageable.unpaged()
        ).getContent();
        
        AuditReportDTO report = new AuditReportDTO();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedBy(generatedBy);
        report.setReportType("AUDIT");
        report.setFromDate(from);
        report.setToDate(to);
        report.setTotalRecords(logs.size());
        
        List<Map<String, Object>> records = logs.stream().map(logItem -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", logItem.getId());
            map.put("action", logItem.getAction());
            map.put("user", logItem.getPerformedBy());
            map.put("timestamp", logItem.getCreatedAt());
            map.put("details", logItem.getRequestData());
            return map;
        }).collect(Collectors.toList());
        
        report.setRecords(records);
        return report;
    }

    @Override
    public Page<Map<String, Object>> generateTransactionReport(LocalDate from, LocalDate to, String type, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByCreatedAtBetween(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                pageable
        );
        
        return transactions.map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("referenceNumber", t.getReferenceNumber());
            map.put("transactionType", t.getTransactionType());
            map.put("amount", t.getAmount());
            map.put("status", t.getStatus());
            map.put("createdAt", t.getCreatedAt());
            return map;
        });
    }

    @Override
    public Map<String, Object> generateLoanPortfolioReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("reportGeneratedAt", LocalDateTime.now());
        report.put("note", "Inject LoanRepository for full implementation");
        return report;
    }

    @Override
    public Map<String, Object> generateDailyTransactionSummary(LocalDate date) {
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(
                date.atStartOfDay(),
                date.atTime(23, 59, 59),
                Pageable.unpaged()
        ).getContent();
        
        long totalTransactions = transactions.size();
        BigDecimal totalAmount = BigDecimal.ZERO;
        long successCount = 0;
        long failedCount = 0;
        Map<String, Long> channelBreakdown = new HashMap<>();
        
        for (Transaction t : transactions) {
            totalAmount = totalAmount.add(t.getAmount());
            if ("SUCCESS".equals(t.getStatus())) {
                successCount++;
            } else if ("FAILED".equals(t.getStatus())) {
                failedCount++;
            }
            
            String channel = t.getChannel() != null ? t.getChannel() : "UNKNOWN";
            channelBreakdown.put(channel, channelBreakdown.getOrDefault(channel, 0L) + 1);
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("date", date);
        summary.put("totalTransactions", totalTransactions);
        summary.put("totalAmount", totalAmount);
        summary.put("successCount", successCount);
        summary.put("failedCount", failedCount);
        summary.put("channelBreakdown", channelBreakdown);
        
        return summary;
    }
}

