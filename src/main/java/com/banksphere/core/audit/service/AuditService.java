package com.banksphere.core.audit.service;

import com.banksphere.core.audit.entity.AuditLog;
import com.banksphere.core.audit.entity.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service interface for handling audit logging.
 */
public interface AuditService {
    void logAction(String entityName, String entityId, String action, String module, Object requestData, Object responseData, boolean success, String errorMessage);
    void logSecurityEvent(String eventType, String userId, String username, boolean success, String failureReason);
    Page<AuditLog> findAuditLogs(String module, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<SecurityAuditLog> findSecurityLogs(String username, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
