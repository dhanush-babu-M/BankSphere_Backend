package com.banksphere.core.audit.aspect;

import com.banksphere.core.audit.annotation.Auditable;
import com.banksphere.core.audit.annotation.SensitiveOperation;
import com.banksphere.core.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for handling @Auditable and @SensitiveOperation logging.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object logAuditableMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        // TODO: implement
        return joinPoint.proceed();
    }

    @Around("@annotation(sensitiveOperation)")
    public Object logSensitiveOperation(ProceedingJoinPoint joinPoint, SensitiveOperation sensitiveOperation) throws Throwable {
        // TODO: implement
        return joinPoint.proceed();
    }

    private String extractCorrelationId() {
        // TODO: implement
        return null;
    }

    private String extractIpAddress() {
        // TODO: implement
        return null;
    }
}
