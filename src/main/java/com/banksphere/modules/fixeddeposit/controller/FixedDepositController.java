package com.banksphere.modules.fixeddeposit.controller;

import com.banksphere.modules.fixeddeposit.dto.CreateFdRequestDTO;
import com.banksphere.modules.fixeddeposit.dto.FdInterestCalculationDTO;
import com.banksphere.modules.fixeddeposit.dto.PrematureFdClosureDTO;
import com.banksphere.modules.fixeddeposit.service.FixedDepositService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/fixed-deposits")
@RequiredArgsConstructor
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;

    @PostMapping("/")
    public ResponseEntity<?> createFd(@Valid @RequestBody CreateFdRequestDTO dto) {
        return ResponseEntity.ok(fixedDepositService.createFd(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFd(@PathVariable UUID id) {
        return ResponseEntity.ok(fixedDepositService.getFd(id));
    }

    @GetMapping("/number/{fdNumber}")
    public ResponseEntity<?> getFdByNumber(@PathVariable String fdNumber) {
        return ResponseEntity.ok(fixedDepositService.getFdByNumber(fdNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerFds(@PathVariable UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(fixedDepositService.getCustomerFds(customerId, pageable));
    }

    @PostMapping("/calculate-interest")
    public ResponseEntity<?> calculateInterest(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal rate,
            @RequestParam int months,
            @RequestParam String fdType) {
        return ResponseEntity.ok(fixedDepositService.calculateInterest(principal, rate, months, fdType));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> prematureClose(@PathVariable UUID id, @Valid @RequestBody PrematureFdClosureDTO dto) {
        dto.setFdId(id);
        return ResponseEntity.ok(fixedDepositService.prematureClose(dto));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renewFd(@PathVariable UUID id) {
        return ResponseEntity.ok(fixedDepositService.renewFd(id));
    }
}
