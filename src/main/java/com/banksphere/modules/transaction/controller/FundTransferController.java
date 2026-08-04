package com.banksphere.modules.transaction.controller;

import com.banksphere.modules.transaction.dto.ExternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.InternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.WireTransferRequestDTO;
import com.banksphere.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class FundTransferController {

    private final TransactionService transactionService;

    @PostMapping("/internal")
    public ResponseEntity<?> initateInternalTransfer(@Valid @RequestBody InternalTransferRequestDTO request) {
        return ResponseEntity.ok(transactionService.initateInternalTransfer(request, "SYSTEM"));
    }

    @PostMapping("/external")
    public ResponseEntity<?> initiateExternalTransfer(@Valid @RequestBody ExternalTransferRequestDTO request) {
        return ResponseEntity.ok(transactionService.initiateExternalTransfer(request, "SYSTEM"));
    }

    @PostMapping("/wire")
    public ResponseEntity<?> initiateWireTransfer(@Valid @RequestBody WireTransferRequestDTO request) {
        return ResponseEntity.ok(transactionService.initiateWireTransfer(request, "SYSTEM"));
    }

    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<?> getTransactionStatus(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(transactionService.getTransactionByReference(referenceNumber));
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<?> reverseTransaction(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(transactionService.reverseTransaction(id, reason, "SYSTEM"));
    }

    @GetMapping("/{referenceNumber}/receipt")
    public ResponseEntity<?> getTransactionReceipt(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(transactionService.getTransactionReceipt(referenceNumber));
    }
}
