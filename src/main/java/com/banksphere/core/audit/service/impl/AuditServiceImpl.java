package com.banksphere.core.audit.service.impl;

import com.banksphere.core.audit.entity.AuditLog;
import com.banksphere.core.audit.entity.SecurityAuditLog;
import com.banksphere.core.audit.repository.AuditLogRepository;
import com.banksphere.core.audit.repository.SecurityAuditLogRepository;
import com.banksphere.core.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of AuditService.
 */
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityAuditLogRepository securityAuditLogRepository;

    @Async
    @Override
    public void logAction(String entityName, String entityId, String action, String module, Object requestData, Object responseData, boolean success, String errorMessage) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Async
    @Override
    public void logSecurityEvent(String eventType, String userId, String username, boolean success, String failureReason) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Page<AuditLog> findAuditLogs(String module, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Page<SecurityAuditLog> findSecurityLogs(String username, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
