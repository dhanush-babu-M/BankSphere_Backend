package com.banksphere.modules.creditcard.repository;

import com.banksphere.modules.creditcard.entity.CreditCardTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, UUID> {
    Page<CreditCardTransaction> findByCreditCardId(UUID creditCardId, Pageable pageable);
    List<CreditCardTransaction> findByCreditCardIdAndTransactionDateBetween(UUID creditCardId, LocalDateTime start, LocalDateTime end);
}
