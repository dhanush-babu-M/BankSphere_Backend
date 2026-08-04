package com.banksphere.modules.payment.repository;

import com.banksphere.modules.payment.entity.PaymentGatewayLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayLogRepository extends JpaRepository<PaymentGatewayLog, UUID> {
    Optional<PaymentGatewayLog> findByReferenceNumber(String referenceNumber);
    Page<PaymentGatewayLog> findBySourceAccountId(UUID sourceAccountId, Pageable pageable);
    Page<PaymentGatewayLog> findByStatus(String status, Pageable pageable);
    List<PaymentGatewayLog> findByStatusAndRetryCountLessThan(String status, int retryCount);
}
