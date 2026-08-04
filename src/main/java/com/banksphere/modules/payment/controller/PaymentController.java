package com.banksphere.modules.payment.controller;

import com.banksphere.modules.payment.dto.BillPaymentRequestDTO;
import com.banksphere.modules.payment.dto.ExternalPaymentRequestDTO;
import com.banksphere.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/external")
    public ResponseEntity<?> initiatExternalPayment(@Valid @RequestBody ExternalPaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.initiatExternalPayment(request, "SYSTEM")); // Replace with actual user
    }

    @PostMapping("/bill")
    public ResponseEntity<?> payBill(@Valid @RequestBody BillPaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.payBill(request, "SYSTEM")); // Replace with actual user
    }

    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(referenceNumber));
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<?> getPaymentHistory(@PathVariable UUID accountId, Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(accountId, pageable));
    }

    @GetMapping("/merchants")
    public ResponseEntity<?> getBillMerchants() {
        return ResponseEntity.ok(paymentService.getBillMerchants());
    }

    @GetMapping("/merchants/category/{category}")
    public ResponseEntity<?> getBillMerchantsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(paymentService.getBillMerchantsByCategory(category));
    }

    @PostMapping("/retry/{referenceNumber}")
    public ResponseEntity<?> retryFailedPayment(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(paymentService.retryFailedPayment(referenceNumber));
    }

    @DeleteMapping("/{referenceNumber}")
    public ResponseEntity<?> cancelPayment(@PathVariable String referenceNumber) {
        paymentService.cancelPayment(referenceNumber);
        return ResponseEntity.noContent().build();
    }
}
