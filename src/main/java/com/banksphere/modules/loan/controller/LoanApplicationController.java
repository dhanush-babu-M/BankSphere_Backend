package com.banksphere.modules.loan.controller;

import com.banksphere.modules.loan.dto.LoanApplicationRequestDTO;
import com.banksphere.modules.loan.dto.LoanApprovalDTO;
import com.banksphere.modules.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loans/applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForLoan(@Valid @RequestBody LoanApplicationRequestDTO dto) {
        return ResponseEntity.ok(loanService.applyForLoan(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getLoanApplication(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerLoanApplications(@PathVariable UUID customerId) {
        return ResponseEntity.ok().build(); 
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveLoan(@Valid @RequestBody LoanApprovalDTO dto) {
        return ResponseEntity.ok(loanService.approveLoan(dto, "admin"));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApplications() {
        return ResponseEntity.ok().build();
    }
}
