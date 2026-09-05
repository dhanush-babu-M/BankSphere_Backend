package com.banksphere.modules.transaction.service;

import com.banksphere.core.exception.InsufficientFundsException;
import com.banksphere.core.exception.InvalidTransactionException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.transaction.dto.InternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.TransactionResponseDTO;
import com.banksphere.modules.transaction.entity.LedgerEntry;
import com.banksphere.modules.transaction.entity.Transaction;
import com.banksphere.modules.transaction.mapper.TransactionMapper;
import com.banksphere.modules.transaction.repository.LedgerEntryRepository;
import com.banksphere.modules.transaction.repository.TransactionRepository;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sourceAccount;
    private Account destAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("BSP2026000000001")
                .status("ACTIVE")
                .balance(new BigDecimal("10000.00"))
                .availableBalance(new BigDecimal("10000.00"))
                .currency("INR")
                .build();

        destAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("BSP2026000000002")
                .status("ACTIVE")
                .balance(new BigDecimal("2000.00"))
                .availableBalance(new BigDecimal("2000.00"))
                .currency("INR")
                .build();
    }

    @Test
    @DisplayName("Initiate internal transfer should debit source, credit dest, and create double ledger entries")
    void initiateInternalTransfer_shouldCreateTransaction_whenBalanceSufficient() {
        InternalTransferRequestDTO request = InternalTransferRequestDTO.builder()
                .sourceAccountNumber("BSP2026000000001")
                .destinationAccountNumber("BSP2026000000002")
                .amount(new BigDecimal("3000.00"))
                .description("Rent Payment")
                .narration("Monthly rent")
                .build();

        when(accountRepository.findByAccountNumber("BSP2026000000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("BSP2026000000002")).thenReturn(Optional.of(destAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .referenceNumber("TXN12345678")
                .amount(new BigDecimal("3000.00"))
                .status("COMPLETED")
                .build();
        when(transactionMapper.toResponseDTO(any(Transaction.class))).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.initateInternalTransfer(request, "USER1");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("COMPLETED");

        // Verify balances
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("7000.00");
        assertThat(destAccount.getBalance()).isEqualByComparingTo("5000.00");

        // Verify double-entry ledger bookkeeping (2 entries: 1 debit, 1 credit)
        verify(ledgerEntryRepository, times(2)).save(any(LedgerEntry.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Initiate internal transfer should throw exception when insufficient funds")
    void initiateInternalTransfer_shouldThrowException_whenInsufficientFunds() {
        InternalTransferRequestDTO request = InternalTransferRequestDTO.builder()
                .sourceAccountNumber("BSP2026000000001")
                .destinationAccountNumber("BSP2026000000002")
                .amount(new BigDecimal("50000.00"))
                .build();

        when(accountRepository.findByAccountNumber("BSP2026000000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("BSP2026000000002")).thenReturn(Optional.of(destAccount));

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.initateInternalTransfer(request, "USER1"));

        verify(transactionRepository, never()).save(any());
        verify(ledgerEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Initiate internal transfer should throw exception when source account is inactive")
    void initiateInternalTransfer_shouldThrowException_whenAccountInactive() {
        sourceAccount.setStatus("FROZEN");
        InternalTransferRequestDTO request = InternalTransferRequestDTO.builder()
                .sourceAccountNumber("BSP2026000000001")
                .destinationAccountNumber("BSP2026000000002")
                .amount(new BigDecimal("500.00"))
                .build();

        when(accountRepository.findByAccountNumber("BSP2026000000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("BSP2026000000002")).thenReturn(Optional.of(destAccount));

        assertThrows(InvalidTransactionException.class,
                () -> transactionService.initateInternalTransfer(request, "USER1"));
    }

    @Test
    @DisplayName("Initiate internal transfer should throw exception when destination account not found")
    void initiateInternalTransfer_shouldThrowException_whenAccountNotFound() {
        InternalTransferRequestDTO request = InternalTransferRequestDTO.builder()
                .sourceAccountNumber("BSP2026000000001")
                .destinationAccountNumber("NON_EXISTENT")
                .amount(new BigDecimal("500.00"))
                .build();

        when(accountRepository.findByAccountNumber("BSP2026000000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("NON_EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.initateInternalTransfer(request, "USER1"));
    }

    @Test
    @DisplayName("Get transaction by reference should return DTO when found")
    void getTransactionByReference_shouldReturnDTO_whenFound() {
        Transaction txn = Transaction.builder()
                .referenceNumber("TXN2026090412345678")
                .amount(new BigDecimal("1000.00"))
                .status("COMPLETED")
                .build();

        TransactionResponseDTO dto = TransactionResponseDTO.builder()
                .referenceNumber("TXN2026090412345678")
                .amount(new BigDecimal("1000.00"))
                .status("COMPLETED")
                .build();

        when(transactionRepository.findByReferenceNumber("TXN2026090412345678")).thenReturn(Optional.of(txn));
        when(transactionMapper.toResponseDTO(txn)).thenReturn(dto);

        TransactionResponseDTO result = transactionService.getTransactionByReference("TXN2026090412345678");

        assertThat(result).isNotNull();
        assertThat(result.getReferenceNumber()).isEqualTo("TXN2026090412345678");
    }

    @Test
    @DisplayName("Reverse transaction should update status to reversed and restore balances")
    void reverseTransaction_shouldUpdateStatusToReversed() {
        UUID txnId = UUID.randomUUID();
        Transaction txn = Transaction.builder()
                .id(txnId)
                .referenceNumber("TXN2026090412345678")
                .accountId(sourceAccount.getId())
                .linkedAccountId(destAccount.getId())
                .transactionType("TRANSFER_OUT")
                .amount(new BigDecimal("1000.00"))
                .status("COMPLETED")
                .build();

        when(transactionRepository.findById(txnId)).thenReturn(Optional.of(txn));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponseDTO dto = TransactionResponseDTO.builder()
                .referenceNumber(txn.getReferenceNumber())
                .status("REVERSED")
                .build();
        when(transactionMapper.toResponseDTO(any(Transaction.class))).thenReturn(dto);

        TransactionResponseDTO result = transactionService.reverseTransaction(txnId, "Customer dispute resolved", "MANAGER_1");

        assertThat(result).isNotNull();
        assertThat(txn.getStatus()).isEqualTo("REVERSED");
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("11000.00");
        verify(transactionRepository).save(txn);
    }
}

