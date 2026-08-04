package com.banksphere.modules.customer.controller;

import com.banksphere.modules.customer.dto.request.CustomerKycDTO;
import com.banksphere.modules.customer.dto.request.CustomerRegistrationDTO;
import com.banksphere.modules.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers/onboarding")
@RequiredArgsConstructor
@Tag(name = "Customer Onboarding", description = "Customer Onboarding APIs")
public class CustomerOnboardingController {

    private final CustomerService customerService;

    @PostMapping("/register")
    @Operation(summary = "Register new customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegistrationDTO request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/kyc")
    @Operation(summary = "Submit KYC document")
    public ResponseEntity<?> submitKyc(@Valid @RequestBody CustomerKycDTO request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/kyc/approve")
    @Operation(summary = "Approve customer KYC")
    public ResponseEntity<?> approveKyc(@PathVariable UUID id, @RequestParam String approvedBy) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/kyc/reject")
    @Operation(summary = "Reject customer KYC")
    public ResponseEntity<?> rejectKyc(@PathVariable UUID id, @RequestParam String reason, @RequestParam String rejectedBy) {
        return ResponseEntity.ok().build();
    }
}
