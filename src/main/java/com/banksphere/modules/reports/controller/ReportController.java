package com.banksphere.modules.reports.controller;

import com.banksphere.modules.reports.dto.StatementRequestDTO;
import com.banksphere.modules.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/statement")
    public ResponseEntity<?> getStatement(
            @RequestParam UUID accountId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            @RequestParam(defaultValue = "JSON") String format,
            @RequestParam(required = false) String transactionType) {

        StatementRequestDTO request = StatementRequestDTO.builder()
                .accountId(accountId)
                .fromDate(fromDate)
                .toDate(toDate)
                .format(format)
                .transactionType(transactionType)
                .build();

        if ("PDF".equalsIgnoreCase(format)) {
            byte[] pdfBytes = reportService.generateAccountStatement(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "statement.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.ok(reportService.generateAccountStatementJson(request));
        }
    }

    @GetMapping("/financial-summary/{customerId}")
    public ResponseEntity<?> getFinancialSummary(
            @PathVariable UUID customerId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        return ResponseEntity.ok(reportService.generateFinancialSummary(customerId, fromDate, toDate));
    }

    @GetMapping("/audit")
    public ResponseEntity<?> getAuditReport(
            @RequestParam String module,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        return ResponseEntity.ok(reportService.generateAuditReport(module, fromDate, toDate, "SYSTEM")); // Replace with actual user
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionsReport(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.generateTransactionReport(fromDate, toDate, type, pageable));
    }

    @GetMapping("/loans/portfolio")
    public ResponseEntity<?> getLoanPortfolioReport() {
        return ResponseEntity.ok(reportService.generateLoanPortfolioReport());
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<?> getDailySummary(@RequestParam LocalDate date) {
        return ResponseEntity.ok(reportService.generateDailyTransactionSummary(date));
    }
}
