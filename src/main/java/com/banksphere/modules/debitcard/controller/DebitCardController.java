package com.banksphere.modules.debitcard.controller;

import com.banksphere.modules.debitcard.dto.request.DebitCardLimitUpdateDTO;
import com.banksphere.modules.debitcard.dto.request.DebitCardPinChangeDTO;
import com.banksphere.modules.debitcard.dto.request.IssueDebitCardRequestDTO;
import com.banksphere.modules.debitcard.service.DebitCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/debit-cards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Debit Card", description = "Debit Card Management APIs")
public class DebitCardController {

    private final DebitCardService debitCardService;

    @PostMapping("/issue")
    @Operation(summary = "Issue a new debit card")
    public ResponseEntity<?> issueDebitCard(@Valid @RequestBody IssueDebitCardRequestDTO request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get debit card details")
    public ResponseEntity<?> getDebitCard(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all debit cards for an account")
    public ResponseEntity<?> getAccountDebitCards(@PathVariable UUID accountId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pin/change")
    @Operation(summary = "Change debit card PIN")
    public ResponseEntity<?> changePin(@Valid @RequestBody DebitCardPinChangeDTO request) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/limits")
    @Operation(summary = "Update debit card limits")
    public ResponseEntity<?> updateLimits(@Valid @RequestBody DebitCardLimitUpdateDTO request) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/block")
    @Operation(summary = "Block debit card")
    public ResponseEntity<?> blockCard(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/hotlist")
    @Operation(summary = "Hotlist/Permanent block debit card")
    public ResponseEntity<?> hotlistCard(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/international")
    @Operation(summary = "Enable/Disable international transactions")
    public ResponseEntity<?> enableInternational(@PathVariable UUID id, @RequestParam boolean enable) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/contactless")
    @Operation(summary = "Enable/Disable contactless transactions")
    public ResponseEntity<?> enableContactless(@PathVariable UUID id, @RequestParam boolean enable) {
        return ResponseEntity.ok().build();
    }
}
