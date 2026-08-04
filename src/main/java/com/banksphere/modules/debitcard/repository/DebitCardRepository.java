package com.banksphere.modules.debitcard.repository;

import com.banksphere.modules.debitcard.entity.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebitCardRepository extends JpaRepository<DebitCard, UUID> {
    List<DebitCard> findByAccountId(UUID accountId);
    List<DebitCard> findByCustomerId(UUID customerId);
    Optional<DebitCard> findByCardNumber(String cardNumber);
    Optional<DebitCard> findByAccountIdAndStatus(UUID accountId, String status);
}
