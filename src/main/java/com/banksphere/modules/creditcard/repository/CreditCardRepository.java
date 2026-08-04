package com.banksphere.modules.creditcard.repository;

import com.banksphere.modules.creditcard.entity.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {
    List<CreditCard> findByCustomerId(UUID customerId);
    Optional<CreditCard> findByCardNumber(String cardNumber);
    Page<CreditCard> findByStatus(String status, Pageable pageable);
}
