package com.banksphere.modules.account.repository;

import com.banksphere.modules.account.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Page<Account> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Account> findByStatus(String status, Pageable pageable);
    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByCustomerIdAndStatus(UUID customerId, String status);
}
