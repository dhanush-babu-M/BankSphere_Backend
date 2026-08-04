package com.banksphere.modules.customer.controller;

import com.banksphere.modules.customer.dto.request.CustomerProfileUpdateDTO;
import com.banksphere.modules.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer Management APIs")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{id}")
    @Operation(summary = "Get customer details")
    public ResponseEntity<?> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers")
    public ResponseEntity<?> searchCustomers(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/profile")
    @Operation(summary = "Update customer profile")
    public ResponseEntity<?> updateProfile(@PathVariable UUID id, @Valid @RequestBody CustomerProfileUpdateDTO request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/kyc-status")
    @Operation(summary = "Get customer KYC status")
    public ResponseEntity<?> getKycStatus(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }
}
