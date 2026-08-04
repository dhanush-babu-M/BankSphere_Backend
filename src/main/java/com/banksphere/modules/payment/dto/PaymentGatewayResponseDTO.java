package com.banksphere.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayResponseDTO {
    private String referenceNumber;
    private String status;
    private String gatewayReferenceId;
    private String message;
    private BigDecimal amount;
    private LocalDateTime completedAt;
    private LocalDateTime estimatedSettlementTime;
}
