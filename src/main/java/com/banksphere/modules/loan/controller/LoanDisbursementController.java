package com.banksphere.modules.loan.controller;

import com.banksphere.modules.loan.dto.EmiPaymentRequestDTO;
import com.banksphere.modules.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanDisbursementController {

    private final LoanService loanService;

    @PostMapping("/{id}/disburse")
    public ResponseEntity<?> disburseLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.disburseLoan(id, "admin"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getLoan(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerLoans(@PathVariable UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(loanService.getCustomerLoans(customerId, pageable));
    }

    @PostMapping("/emi/pay")
    public ResponseEntity<?> payEmi(@Valid @RequestBody EmiPaymentRequestDTO dto) {
        return ResponseEntity.ok(loanService.payEmi(dto));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> getLoanSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/amortization")
    public ResponseEntity<?> getAmortizationSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getAmortizationSchedule(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeLoan(@PathVariable UUID id) {
        loanService.closeLoan(id);
        return ResponseEntity.ok().build();
    }
}
