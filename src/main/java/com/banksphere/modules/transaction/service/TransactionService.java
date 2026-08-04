package com.banksphere.modules.transaction.service;

import com.banksphere.modules.transaction.dto.ExternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.InternalTransferRequestDTO;
import com.banksphere.modules.transaction.dto.TransactionReceiptDTO;
import com.banksphere.modules.transaction.dto.TransactionResponseDTO;
import com.banksphere.modules.transaction.dto.WireTransferRequestDTO;
import com.banksphere.modules.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionService {
    TransactionResponseDTO initateInternalTransfer(InternalTransferRequestDTO request, String initiatedBy);
    TransactionResponseDTO initiateExternalTransfer(ExternalTransferRequestDTO request, String initiatedBy);
    TransactionResponseDTO initiateWireTransfer(WireTransferRequestDTO request, String initiatedBy);
    TransactionResponseDTO getTransaction(UUID id);
    TransactionResponseDTO getTransactionByReference(String referenceNumber);
    Page<TransactionResponseDTO> getAccountTransactions(UUID accountId, Pageable pageable);
    Page<TransactionResponseDTO> getAccountTransactionsByDateRange(UUID accountId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    TransactionResponseDTO reverseTransaction(UUID transactionId, String reason, String reversedBy);
    TransactionReceiptDTO getTransactionReceipt(String referenceNumber);
    Transaction creditAccount(UUID accountId, BigDecimal amount, String description, String initiatedBy);
    Transaction debitAccount(UUID accountId, BigDecimal amount, String description, String initiatedBy);
}
