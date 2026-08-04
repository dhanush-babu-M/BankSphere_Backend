package com.banksphere.modules.account.controller;

import com.banksphere.modules.account.dto.request.AccountOverdraftUpdateRequestDTO;
import com.banksphere.modules.account.dto.request.AccountStatusUpdateRequestDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts/manage")
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
@RequiredArgsConstructor
@Tag(name = "Account Management API")
@Slf4j
public class AccountManagementController {

    private final AccountService accountService;

    @PutMapping("/status")
    @Operation(summary = "Update account status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(@RequestBody AccountStatusUpdateRequestDTO request) {
        return ResponseEntity.ok(accountService.updateAccountStatus(request));
    }

    @PutMapping("/overdraft")
    @Operation(summary = "Update overdraft limit")
    public ResponseEntity<AccountResponseDTO> updateOverdraftLimit(@RequestBody AccountOverdraftUpdateRequestDTO request) {
        return ResponseEntity.ok(accountService.updateOverdraftLimit(request));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all accounts")
    public ResponseEntity<Page<AccountResponseDTO>> getAllAccounts(Pageable pageable) {
        return ResponseEntity.ok(Page.empty());
    }
}
