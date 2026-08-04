package com.banksphere.modules.account.repository;

import com.banksphere.modules.account.entity.AccountHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountHoldRepository extends JpaRepository<AccountHold, UUID> {
    List<AccountHold> findByAccountIdAndReleasedFalse(UUID accountId);
    List<AccountHold> findByAccountIdAndReleasedFalseAndExpiryDateTimeAfter(UUID accountId, LocalDateTime dateTime);
}
