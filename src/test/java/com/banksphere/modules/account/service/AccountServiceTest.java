package com.banksphere.modules.account.service;

import com.banksphere.core.exception.InsufficientFundsException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.utility.AccountNumberGenerator;
import com.banksphere.modules.account.dto.request.CreateAccountRequestDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.modules.account.mapper.AccountMapper;
import com.banksphere.modules.account.repository.AccountBalanceHistoryRepository;
import com.banksphere.modules.account.repository.AccountHoldRepository;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.account.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountBalanceHistoryRepository accountBalanceHistoryRepository;

    @Mock
    private AccountHoldRepository accountHoldRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account sampleAccount;
    private AccountResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        sampleAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("BSP2026123456789")
                .customerId(UUID.randomUUID())
                .accountType("SAVINGS")
                .status("ACTIVE")
                .balance(new BigDecimal("5000.00"))
                .availableBalance(new BigDecimal("5000.00"))
                .currency("INR")
                .branchCode("001")
                .overdraftLimit(BigDecimal.ZERO)
                .build();

        sampleResponseDTO = AccountResponseDTO.builder()
                .id(sampleAccount.getId())
                .accountNumber(sampleAccount.getAccountNumber())
                .customerId(sampleAccount.getCustomerId())
                .accountType(sampleAccount.getAccountType())
                .status(sampleAccount.getStatus())
                .balance(sampleAccount.getBalance())
                .availableBalance(sampleAccount.getAvailableBalance())
                .currency(sampleAccount.getCurrency())
                .build();
    }

    @Test
    @DisplayName("Create account should save account and return response DTO")
    void createAccount_shouldSaveAndReturnDTO() {
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .customerId(sampleAccount.getCustomerId())
                .accountType("SAVINGS")
                .currency("INR")
                .branchCode("001")
                .initialDeposit(new BigDecimal("1000.00"))
                .build();

        when(accountNumberGenerator.generateAccountNumber()).thenReturn("BSP2026123456789");
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);
        when(accountMapper.toResponseDTO(sampleAccount)).thenReturn(sampleResponseDTO);

        AccountResponseDTO result = accountService.createAccount(request, "TEST_USER");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("BSP2026123456789");
        verify(accountRepository).save(any(Account.class));
        verify(accountBalanceHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Get account by number should return DTO when found")
    void getAccountByNumber_shouldReturnDTO_whenFound() {
        when(accountRepository.findByAccountNumber("BSP2026123456789")).thenReturn(Optional.of(sampleAccount));
        when(accountMapper.toResponseDTO(sampleAccount)).thenReturn(sampleResponseDTO);

        AccountResponseDTO result = accountService.getAccountByNumber("BSP2026123456789");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("BSP2026123456789");
        verify(accountRepository).findByAccountNumber("BSP2026123456789");
    }

    @Test
    @DisplayName("Get account by number should throw not found exception when not found")
    void getAccountByNumber_shouldThrowNotFoundException_whenNotFound() {
        when(accountRepository.findByAccountNumber("NON_EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByNumber("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Credit account should increase balance and available balance")
    void creditAccount_shouldIncreaseBalance() {
        when(accountRepository.findByAccountNumber("BSP2026123456789")).thenReturn(Optional.of(sampleAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.creditAccount("BSP2026123456789", new BigDecimal("1500.00"), "Salary Credit");

        assertThat(sampleAccount.getBalance()).isEqualByComparingTo("6500.00");
        assertThat(sampleAccount.getAvailableBalance()).isEqualByComparingTo("6500.00");
        verify(accountRepository).save(sampleAccount);
        verify(accountBalanceHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Debit account should decrease balance when sufficient funds exist")
    void debitAccount_shouldDecreaseBalance() {
        when(accountRepository.findByAccountNumber("BSP2026123456789")).thenReturn(Optional.of(sampleAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.debitAccount("BSP2026123456789", new BigDecimal("2000.00"), "ATM Withdrawal");

        assertThat(sampleAccount.getBalance()).isEqualByComparingTo("3000.00");
        assertThat(sampleAccount.getAvailableBalance()).isEqualByComparingTo("3000.00");
        verify(accountRepository).save(sampleAccount);
        verify(accountBalanceHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Debit account should throw exception when insufficient funds")
    void debitAccount_shouldThrowException_whenInsufficientFunds() {
        when(accountRepository.findByAccountNumber("BSP2026123456789")).thenReturn(Optional.of(sampleAccount));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.debitAccount("BSP2026123456789", new BigDecimal("10000.00"), "Big Purchase"));

        verify(accountRepository, never()).save(any());
    }
}

