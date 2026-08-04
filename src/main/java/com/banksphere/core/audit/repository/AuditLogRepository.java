package com.banksphere.core.audit.repository;

import com.banksphere.core.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByEntityNameAndEntityId(String entityName, String entityId, Pageable pageable);
    Page<AuditLog> findByPerformedByAndCreatedAtBetween(String performedBy, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<AuditLog> findByModuleAndCreatedAtBetween(String module, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
