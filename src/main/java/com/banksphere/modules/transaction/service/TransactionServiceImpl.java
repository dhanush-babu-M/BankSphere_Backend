package com.banksphere.modules.transaction.service;

import com.banksphere.core.exception.InsufficientFundsException;
import com.banksphere.core.exception.InvalidTransactionException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.transaction.dto.ExternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.InternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.TransactionReceiptDTO;
import com.banksphere.modules.transaction.dto.TransactionResponseDTO;
import com.banksphere.modules.transaction.dto.WireTransferRequestDTO;
import com.banksphere.modules.transaction.entity.LedgerEntry;
import com.banksphere.modules.transaction.entity.Transaction;
import com.banksphere.modules.transaction.mapper.TransactionMapper;
import com.banksphere.modules.transaction.repository.LedgerEntryRepository;
import com.banksphere.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public TransactionResponseDTO initateInternalTransfer(InternalTransferRequestDTO request, String initiatedBy) {
        log.info("Initiating internal transfer from {} to {} amount {}",
                request.getSourceAccountNumber(), request.getDestinationAccountNumber(), request.getAmount());
        
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getSourceAccountNumber()));
        Account destination = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getDestinationAccountNumber()));
        
        validateTransfer(source, request.getAmount());
        
        BigDecimal sourceBefore = source.getBalance();
        BigDecimal destBefore = destination.getBalance();
        
        // Debit source
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        source.setAvailableBalance(source.getAvailableBalance().subtract(request.getAmount()));
        source.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(source);
        
        // Credit destination
        destination.setBalance(destination.getBalance().add(request.getAmount()));
        destination.setAvailableBalance(destination.getAvailableBalance().add(request.getAmount()));
        destination.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(destination);
        
        // Create transaction record
        String refNum = generateReferenceNumber();
        Transaction txn = Transaction.builder()
                .referenceNumber(refNum)
                .accountId(source.getId())
                .linkedAccountId(destination.getId())
                .transactionType("TRANSFER_OUT")
                .amount(request.getAmount())
                .currency(source.getCurrency())
                .description(request.getDescription())
                .narration(request.getNarration())
                .balanceBefore(sourceBefore)
                .balanceAfter(source.getBalance())
                .status("COMPLETED")
                .channel("NET_BANKING")
                .beneficiaryAccountNumber(maskAccountNumber(destination.getAccountNumber()))
                .paymentMode("INTERNAL")
                .valueDate(LocalDate.now())
                .settlementDate(LocalDate.now())
                .initiatedBy(initiatedBy)
                .build();
        transactionRepository.save(txn);
        
        // Create ledger entries
        createLedgerEntries(txn, source, destination, sourceBefore, destBefore);
        
        log.info("Internal transfer completed. Reference: {}", refNum);
        return transactionMapper.toResponseDTO(txn);
    }

    @Override
    public TransactionResponseDTO initiateExternalTransfer(ExternalTransferRequestDTO request, String initiatedBy) {
        log.info("Initiating external transfer from {} amount {}",
                request.getSourceAccountNumber(), request.getAmount());
        
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getSourceAccountNumber()));
        
        validateTransfer(source, request.getAmount());
        
        // Validate RTGS minimum
        if ("RTGS".equalsIgnoreCase(request.getPaymentMode()) && request.getAmount().compareTo(new BigDecimal("200000")) < 0) {
            throw new InvalidTransactionException("TXN_001", "RTGS minimum transfer amount is ₹2,00,000");
        }
        
        BigDecimal balanceBefore = source.getBalance();
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        source.setAvailableBalance(source.getAvailableBalance().subtract(request.getAmount()));
        source.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(source);
        
        String refNum = generateReferenceNumber();
        Transaction txn = Transaction.builder()
                .referenceNumber(refNum)
                .accountId(source.getId())
                .transactionType("TRANSFER_OUT")
                .amount(request.getAmount())
                .currency("INR")
                .narration(request.getNarration())
                .balanceBefore(balanceBefore)
                .balanceAfter(source.getBalance())
                .status("PENDING")
                .channel("NET_BANKING")
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAccountNumber(maskAccountNumber(request.getDestinationAccountNumber()))
                .ifscCode(request.getDestinationIfsc())
                .paymentMode(request.getPaymentMode())
                .valueDate(LocalDate.now())
                .settlementDate(calculateSettlementDate(request.getPaymentMode()))
                .initiatedBy(initiatedBy)
                .build();
        transactionRepository.save(txn);
        
        log.info("External transfer initiated. Reference: {}", refNum);
        return transactionMapper.toResponseDTO(txn);
    }

    @Override
    public TransactionResponseDTO initiateWireTransfer(WireTransferRequestDTO request, String initiatedBy) {
        log.info("Initiating wire transfer from {} to SWIFT {} amount {} {}",
                request.getSourceAccountNumber(), request.getSwiftCode(), request.getAmount(), request.getCurrency());
        
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getSourceAccountNumber()));
        
        validateTransfer(source, request.getAmount());
        
        BigDecimal balanceBefore = source.getBalance();
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        source.setAvailableBalance(source.getAvailableBalance().subtract(request.getAmount()));
        source.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(source);
        
        String refNum = generateReferenceNumber();
        Transaction txn = Transaction.builder()
                .referenceNumber(refNum)
                .accountId(source.getId())
                .transactionType("WIRE_TRANSFER")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .narration(request.getRemarks())
                .balanceBefore(balanceBefore)
                .balanceAfter(source.getBalance())
                .status("PENDING")
                .channel("NET_BANKING")
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAccountNumber(maskAccountNumber(request.getDestinationAccountNumber()))
                .paymentMode("WIRE")
                .valueDate(LocalDate.now())
                .settlementDate(LocalDate.now().plusDays(2))
                .initiatedBy(initiatedBy)
                .build();
        transactionRepository.save(txn);
        
        log.info("Wire transfer initiated. Reference: {}", refNum);
        return transactionMapper.toResponseDTO(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransaction(UUID id) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return transactionMapper.toResponseDTO(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionByReference(String referenceNumber) {
        Transaction txn = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "referenceNumber", referenceNumber));
        return transactionMapper.toResponseDTO(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getAccountTransactions(UUID accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getAccountTransactionsByDateRange(
            UUID accountId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return transactionRepository.findByAccountIdAndCreatedAtBetween(accountId, start, end, pageable)
                .map(transactionMapper::toResponseDTO);
    }

    @Override
    public TransactionResponseDTO reverseTransaction(UUID transactionId, String reason, String reversedBy) {
        log.info("Reversing transaction: {} by {}", transactionId, reversedBy);
        Transaction original = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        
        if ("REVERSED".equals(original.getStatus()) || "FAILED".equals(original.getStatus())) {
            throw new InvalidTransactionException("TXN_001", "Transaction cannot be reversed in its current state: " + original.getStatus());
        }
        
        Account account = accountRepository.findById(original.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", original.getAccountId()));
        
        // Determine reversal direction
        if ("TRANSFER_OUT".equals(original.getTransactionType()) || "DEBIT".equals(original.getTransactionType())) {
            account.setBalance(account.getBalance().add(original.getAmount()));
            account.setAvailableBalance(account.getAvailableBalance().add(original.getAmount()));
        } else {
            if (account.getBalance().compareTo(original.getAmount()) < 0) {
                throw new InsufficientFundsException();
            }
            account.setBalance(account.getBalance().subtract(original.getAmount()));
            account.setAvailableBalance(account.getAvailableBalance().subtract(original.getAmount()));
        }
        accountRepository.save(account);
        
        original.setStatus("REVERSED");
        original.setReversalReason(reason);
        original.setReversedAt(LocalDateTime.now());
        original.setApprovedBy(reversedBy);
        transactionRepository.save(original);
        
        log.info("Transaction reversed successfully: {}", transactionId);
        return transactionMapper.toResponseDTO(original);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionReceiptDTO getTransactionReceipt(String referenceNumber) {
        Transaction txn = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "referenceNumber", referenceNumber));
        Account account = accountRepository.findById(txn.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", txn.getAccountId()));
        
        BigDecimal charges = BigDecimal.ZERO; // TODO: compute based on payment mode
        return TransactionReceiptDTO.builder()
                .referenceNumber(txn.getReferenceNumber())
                .transactionDate(txn.getCreatedAt())
                .senderAccountNumber(maskAccountNumber(account.getAccountNumber()))
                .receiverName(txn.getBeneficiaryName())
                .receiverAccountNumber(txn.getBeneficiaryAccountNumber())
                .amount(txn.getAmount())
                .currency(txn.getCurrency())
                .paymentMode(txn.getPaymentMode())
                .status(txn.getStatus())
                .narration(txn.getNarration())
                .charges(charges)
                .netAmount(txn.getAmount().add(charges))
                .build();
    }

    @Override
    public Transaction creditAccount(UUID accountId, BigDecimal amount, String description, String initiatedBy) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(account.getBalance().add(amount));
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
        account.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(account);
        String refNum = generateReferenceNumber();
        Transaction txn = Transaction.builder()
                .referenceNumber(refNum)
                .accountId(accountId)
                .transactionType("CREDIT")
                .amount(amount)
                .currency(account.getCurrency())
                .description(description)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .status("COMPLETED")
                .channel("SCHEDULER")
                .paymentMode("INTERNAL")
                .valueDate(LocalDate.now())
                .settlementDate(LocalDate.now())
                .initiatedBy(initiatedBy)
                .build();
        return transactionRepository.save(txn);
    }

    @Override
    public Transaction debitAccount(UUID accountId, BigDecimal amount, String description, String initiatedBy) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(account.getBalance().subtract(amount));
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        account.setLastTransactionDate(LocalDateTime.now());
        accountRepository.save(account);
        String refNum = generateReferenceNumber();
        Transaction txn = Transaction.builder()
                .referenceNumber(refNum)
                .accountId(accountId)
                .transactionType("DEBIT")
                .amount(amount)
                .currency(account.getCurrency())
                .description(description)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .status("COMPLETED")
                .channel("SCHEDULER")
                .paymentMode("INTERNAL")
                .valueDate(LocalDate.now())
                .settlementDate(LocalDate.now())
                .initiatedBy(initiatedBy)
                .build();
        return transactionRepository.save(txn);
    }

    // ======================== PRIVATE HELPERS ========================

    private void validateTransfer(Account account, BigDecimal amount) {
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new InvalidTransactionException("ACCT_003", "Account is not active: " + account.getAccountNumber());
        }
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("TXN_001", "Transfer amount must be greater than zero");
        }
    }

    private String generateReferenceNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = RANDOM.nextInt(90000000) + 10000000;
        return "TXN" + date + rand;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return accountNumber;
        return "*".repeat(accountNumber.length() - 4) + accountNumber.substring(accountNumber.length() - 4);
    }

    private LocalDate calculateSettlementDate(String paymentMode) {
        return switch (paymentMode.toUpperCase()) {
            case "RTGS", "IMPS", "UPI" -> LocalDate.now();
            case "NEFT" -> LocalDate.now().plusDays(1);
            default -> LocalDate.now();
        };
    }

    private void createLedgerEntries(Transaction txn, Account debitAccount, Account creditAccount,
                                     BigDecimal debitBefore, BigDecimal creditBefore) {
        LedgerEntry debitEntry = LedgerEntry.builder()
                .transactionId(txn.getId())
                .accountId(debitAccount.getId())
                .entryType("DEBIT")
                .amount(txn.getAmount())
                .runningBalance(debitAccount.getBalance())
                .valueDate(LocalDate.now())
                .description(txn.getDescription())
                .referenceNumber(txn.getReferenceNumber())
                .glAccountCode("10001")
                .build();
        ledgerEntryRepository.save(debitEntry);

        LedgerEntry creditEntry = LedgerEntry.builder()
                .transactionId(txn.getId())
                .accountId(creditAccount.getId())
                .entryType("CREDIT")
                .amount(txn.getAmount())
                .runningBalance(creditAccount.getBalance())
                .valueDate(LocalDate.now())
                .description(txn.getDescription())
                .referenceNumber(txn.getReferenceNumber())
                .glAccountCode("20001")
                .build();
        ledgerEntryRepository.save(creditEntry);
    }
}
