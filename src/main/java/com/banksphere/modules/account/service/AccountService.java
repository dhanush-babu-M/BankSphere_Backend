package com.banksphere.modules.account.service;

import com.banksphere.modules.account.dto.request.AccountOverdraftUpdateRequestDTO;
import com.banksphere.modules.account.dto.request.AccountStatusUpdateRequestDTO;
import com.banksphere.modules.account.dto.request.CreateAccountRequestDTO;
import com.banksphere.modules.account.dto.response.AccountBalanceResponseDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.dto.response.AccountSummaryDTO;
import com.banksphere.modules.account.entity.AccountHold;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDTO createAccount(CreateAccountRequestDTO request, String createdBy);
    AccountResponseDTO getAccountById(UUID id);
    AccountResponseDTO getAccountByNumber(String accountNumber);
    Page<AccountResponseDTO> getCustomerAccounts(UUID customerId, Pageable pageable);
    AccountBalanceResponseDTO getAccountBalance(String accountNumber);
    AccountResponseDTO updateAccountStatus(AccountStatusUpdateRequestDTO request);
    AccountResponseDTO updateOverdraftLimit(AccountOverdraftUpdateRequestDTO request);
    void creditAccount(String accountNumber, BigDecimal amount, String reason);
    void debitAccount(String accountNumber, BigDecimal amount, String reason);
    AccountHold holdAmount(String accountNumber, BigDecimal amount, String reason);
    void releaseHold(UUID holdId);
    List<AccountSummaryDTO> getAccountSummaries(UUID customerId);
}
