package com.banksphere.modules.account.repository;

import com.banksphere.modules.account.entity.AccountBalanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountBalanceHistoryRepository extends JpaRepository<AccountBalanceHistory, UUID> {
    Page<AccountBalanceHistory> findByAccountId(UUID accountId, Pageable pageable);
    List<AccountBalanceHistory> findByAccountIdAndCreatedAtBetween(UUID accountId, LocalDateTime start, LocalDateTime end);
}
