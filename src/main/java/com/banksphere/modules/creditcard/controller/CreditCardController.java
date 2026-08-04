package com.banksphere.modules.creditcard.controller;

import com.banksphere.modules.creditcard.dto.request.CreditCardApplicationDTO;
import com.banksphere.modules.creditcard.dto.request.CreditCardPaymentDTO;
import com.banksphere.modules.creditcard.service.CreditCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/credit-cards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Credit Card", description = "Credit Card Management APIs")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a new credit card")
    public ResponseEntity<?> applyCreditCard(@Valid @RequestBody CreditCardApplicationDTO request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get credit card details")
    public ResponseEntity<?> getCreditCard(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all credit cards for a customer")
    public ResponseEntity<?> getCustomerCreditCards(@PathVariable UUID customerId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment")
    @Operation(summary = "Make a credit card payment")
    public ResponseEntity<?> makeCreditCardPayment(@Valid @RequestBody CreditCardPaymentDTO request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/statement")
    @Operation(summary = "Get credit card statement")
    public ResponseEntity<?> getStatement(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/block")
    @Operation(summary = "Block credit card")
    public ResponseEntity<?> blockCard(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unblock")
    @Operation(summary = "Unblock credit card")
    public ResponseEntity<?> unblockCard(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "Get credit card transactions")
    public ResponseEntity<?> getTransactions(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok().build();
    }
}
