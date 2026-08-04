package com.banksphere.modules.creditcard.repository;

import com.banksphere.modules.creditcard.entity.CreditCardBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditCardBillRepository extends JpaRepository<CreditCardBill, UUID> {
    Page<CreditCardBill> findByCreditCardId(UUID creditCardId, Pageable pageable);
    List<CreditCardBill> findByCreditCardIdAndStatus(UUID creditCardId, String status);
}
