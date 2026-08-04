package com.banksphere.core.audit.repository;

import com.banksphere.core.audit.entity.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {
    Page<SecurityAuditLog> findByUsernameAndCreatedAtBetween(String username, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<SecurityAuditLog> findByEventTypeAndSuccess(String eventType, Boolean success, Pageable pageable);
    long countByUsernameAndSuccessAndCreatedAtAfter(String username, Boolean success, LocalDateTime after);
}
