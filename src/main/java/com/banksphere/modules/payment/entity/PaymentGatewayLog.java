package com.banksphere.modules.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_gateway_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentGatewayLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String referenceNumber; // unique

    private String paymentType; // NEFT/RTGS/IMPS/UPI/BILL_PAYMENT/EXTERNAL

    private UUID sourceAccountId;

    private String destinationDetails;

    private BigDecimal amount;

    @Builder.Default
    private String currency = "INR";

    private String gatewayProvider;

    private String gatewayReferenceId;

    private String status; // INITIATED/PENDING/SUCCESS/FAILED/REFUNDED

    private String failureReason;

    @Lob
    private String requestPayload;

    @Lob
    private String responsePayload;

    @CreatedDate
    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    @Builder.Default
    private int retryCount = 0;
}
