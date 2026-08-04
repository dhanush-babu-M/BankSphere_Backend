package com.banksphere.modules.account.service.impl;

import com.banksphere.core.exception.*;
import com.banksphere.core.utility.AccountNumberGenerator;
import com.banksphere.modules.account.entity.*;
import com.banksphere.modules.account.repository.*;
import com.banksphere.modules.account.dto.request.*;
import com.banksphere.modules.account.dto.response.*;
import java.util.UUID;
import com.banksphere.core.constants.AccountStatus;
import com.banksphere.modules.account.mapper.AccountMapper;
import com.banksphere.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountBalanceHistoryRepository accountBalanceHistoryRepository;
    private final AccountHoldRepository accountHoldRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO request, String createdBy) {
        log.info("Creating new account for customerId: {}", request.getCustomerId());
        
        String accountNumber = accountNumberGenerator.generateAccountNumber();
        BigDecimal initialDeposit = request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO;
        
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .status("ACTIVE")
                .balance(initialDeposit)
                .availableBalance(initialDeposit)
                .currency(request.getCurrency())
                .branchCode(request.getBranchCode())
                .ifscCode("BANK" + request.getBranchCode())
                .interestRate(BigDecimal.valueOf(0.03))
                .overdraftLimit(BigDecimal.ZERO)
                .createdBy(createdBy)
                .build();
                
        Account savedAccount = accountRepository.save(account);
        
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            AccountBalanceHistory history = AccountBalanceHistory.builder()
                    .accountId(savedAccount.getId())
                    .previousBalance(BigDecimal.ZERO)
                    .newBalance(initialDeposit)
                    .changedBy("SYSTEM")
                    .changeReason("Initial deposit")
                    .createdAt(LocalDateTime.now())
                    .build();
            accountBalanceHistoryRepository.save(history);
        }
        
        return accountMapper.toResponseDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return accountMapper.toResponseDTO(account);
    }

    @Override
    public AccountResponseDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        return accountMapper.toResponseDTO(account);
    }

    @Override
    public Page<AccountResponseDTO> getCustomerAccounts(UUID customerId, Pageable pageable) {
        return accountRepository.findByCustomerId(customerId, pageable)
                .map(accountMapper::toResponseDTO);
    }

    @Override
    public AccountBalanceResponseDTO getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
                
        BigDecimal holdAmount = accountHoldRepository.findByAccountIdAndReleasedFalseAndExpiryDateTimeAfter(
                account.getId(), LocalDateTime.now())
                .stream()
                .map(AccountHold::getHoldAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        return AccountBalanceResponseDTO.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .currentBalance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .holdAmount(holdAmount)
                .currency(account.getCurrency())
                .asOfDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public AccountResponseDTO updateAccountStatus(AccountStatusUpdateRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
                
        AccountStatus currentStatus = AccountStatus.valueOf(account.getStatus());
        AccountStatus newStatus = AccountStatus.valueOf(request.getNewStatus());
        
        if (currentStatus == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed account");
        }
        
        if (currentStatus == AccountStatus.FROZEN && newStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Frozen account can only be activated");
        }
        
        account.setStatus(newStatus.name());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO updateOverdraftLimit(AccountOverdraftUpdateRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
                
        account.setOverdraftLimit(request.getOverdraftLimit());
        account = accountRepository.save(account);
        return accountMapper.toResponseDTO(account);
    }

    @Override
    public void creditAccount(String accountNumber, BigDecimal amount, String reason) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
                
        BigDecimal oldBalance = account.getBalance();
        
        account.setBalance(account.getBalance().add(amount));
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
        account.setLastTransactionDate(LocalDateTime.now());
        
        Account savedAccount = accountRepository.save(account);
        
        AccountBalanceHistory history = AccountBalanceHistory.builder()
                .accountId(savedAccount.getId())
                .previousBalance(oldBalance)
                .newBalance(savedAccount.getBalance())
                .changedBy("SYSTEM")
                .changeReason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        accountBalanceHistoryRepository.save(history);
    }

    @Override
    public void debitAccount(String accountNumber, BigDecimal amount, String reason) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
                
        if (account.getAvailableBalance().add(account.getOverdraftLimit()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for this transaction");
        }
        
        BigDecimal oldBalance = account.getBalance();
        
        account.setBalance(account.getBalance().subtract(amount));
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        account.setLastTransactionDate(LocalDateTime.now());
        
        Account savedAccount = accountRepository.save(account);
        
        AccountBalanceHistory history = AccountBalanceHistory.builder()
                .accountId(savedAccount.getId())
                .previousBalance(oldBalance)
                .newBalance(savedAccount.getBalance())
                .changedBy("SYSTEM")
                .changeReason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        accountBalanceHistoryRepository.save(history);
    }

    @Override
    public AccountHold holdAmount(String accountNumber, BigDecimal amount, String reason) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
                
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient available balance for hold");
        }
        
        AccountHold hold = AccountHold.builder()
                .accountId(account.getId())
                .holdAmount(amount)
                .holdReason(reason)
                .expiryDateTime(LocalDateTime.now().plusHours(24))
                .released(false)
                .build();
                
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        accountRepository.save(account);
        
        return accountHoldRepository.save(hold);
    }

    @Override
    public void releaseHold(UUID holdId) {
        AccountHold hold = accountHoldRepository.findById(holdId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountHold", "id", holdId));
                
        if (hold.isReleased()) {
            throw new IllegalStateException("Hold is already released");
        }
        
        hold.setReleased(true);
        hold.setReleasedAt(LocalDateTime.now());
        accountHoldRepository.save(hold);
        
        Account account = accountRepository.findById(hold.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", hold.getAccountId()));
                
        account.setAvailableBalance(account.getAvailableBalance().add(hold.getHoldAmount()));
        accountRepository.save(account);
    }

    @Override
    public List<AccountSummaryDTO> getAccountSummaries(UUID customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        return accounts.stream()
                .map(accountMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }
}
