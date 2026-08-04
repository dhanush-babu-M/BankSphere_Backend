package com.banksphere.modules.transaction.repository;

import com.banksphere.modules.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    Page<Transaction> findByAccountIdAndCreatedAtBetween(UUID accountId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Transaction> findByAccountIdAndTransactionType(UUID accountId, String transactionType, Pageable pageable);
    Page<Transaction> findByStatus(String status, Pageable pageable);
    List<Transaction> findByStatusAndCreatedAtBefore(String status, LocalDateTime date);
    Page<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionType = :type AND t.createdAt >= :from AND t.createdAt <= :to")
    BigDecimal sumAmountByAccountIdAndTypeAndDateRange(@Param("accountId") UUID accountId, @Param("type") String type, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
