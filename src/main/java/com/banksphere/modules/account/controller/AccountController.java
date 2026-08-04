package com.banksphere.modules.account.controller;

import com.banksphere.modules.account.dto.request.CreateAccountRequestDTO;
import com.banksphere.modules.account.dto.response.AccountBalanceResponseDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.dto.response.AccountSummaryDTO;
import com.banksphere.modules.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Account API")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by number")
    public ResponseEntity<AccountResponseDTO> getAccountByNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<AccountBalanceResponseDTO> getAccountBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountBalance(accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer accounts")
    public ResponseEntity<Page<AccountResponseDTO>> getCustomerAccounts(@PathVariable UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(accountService.getCustomerAccounts(customerId, pageable));
    }

    @PostMapping
    @Operation(summary = "Create new account")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody CreateAccountRequestDTO request) {
        return ResponseEntity.ok(accountService.createAccount(request, "system"));
    }

    @GetMapping("/{accountNumber}/summary")
    @Operation(summary = "Get account summary")
    public ResponseEntity<List<AccountSummaryDTO>> getAccountSummaries(@PathVariable UUID customerId) {
        return ResponseEntity.ok(accountService.getAccountSummaries(customerId));
    }
}
