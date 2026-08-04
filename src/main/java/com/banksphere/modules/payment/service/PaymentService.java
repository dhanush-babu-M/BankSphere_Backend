package com.banksphere.modules.payment.service;

import com.banksphere.modules.payment.dto.BillPaymentRequestDTO;
import com.banksphere.modules.payment.dto.ExternalPaymentRequestDTO;
import com.banksphere.modules.payment.dto.PaymentGatewayResponseDTO;
import com.banksphere.modules.payment.entity.BillMerchant;
import com.banksphere.modules.payment.entity.PaymentGatewayLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentGatewayResponseDTO initiatExternalPayment(ExternalPaymentRequestDTO request, String initiatedBy);
    PaymentGatewayResponseDTO payBill(BillPaymentRequestDTO request, String initiatedBy);
    PaymentGatewayResponseDTO getPaymentStatus(String referenceNumber);
    Page<PaymentGatewayLog> getPaymentHistory(UUID accountId, Pageable pageable);
    List<BillMerchant> getBillMerchants();
    List<BillMerchant> getBillMerchantsByCategory(String category);
    PaymentGatewayResponseDTO retryFailedPayment(String referenceNumber);
    PaymentGatewayResponseDTO cancelPayment(String referenceNumber);
}
