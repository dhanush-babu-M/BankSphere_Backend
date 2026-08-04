package com.banksphere.modules.payment.service;

import com.banksphere.modules.payment.dto.ExternalPaymentRequestDTO;
import com.banksphere.modules.payment.dto.PaymentGatewayResponseDTO;
import com.banksphere.modules.payment.dto.BillPaymentRequestDTO;
import com.banksphere.modules.payment.entity.PaymentGatewayLog;
import com.banksphere.modules.payment.entity.BillMerchant;
import com.banksphere.modules.payment.mapper.PaymentMapper;
import com.banksphere.modules.payment.repository.PaymentGatewayLogRepository;
import com.banksphere.modules.payment.repository.BillMerchantRepository;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentGatewayLogRepository paymentGatewayLogRepository;
    private final BillMerchantRepository billMerchantRepository;
    private final PaymentMapper paymentMapper;
    private final AccountRepository accountRepository;

    @Override
    public PaymentGatewayResponseDTO initiatExternalPayment(ExternalPaymentRequestDTO request, String initiatedBy) {
        Account account = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getSourceAccountNumber()));
                
        if (!"ACTIVE".equals(account.getStatus()) || account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Invalid account status or insufficient balance");
        }
        
        String referenceNumber = "PAY" + System.currentTimeMillis();
        
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        
        PaymentGatewayLog log = new PaymentGatewayLog();
        log.setReferenceNumber(referenceNumber);
        log.setPaymentType(request.getPaymentType());
        log.setSourceAccountId(account.getId());
        log.setDestinationDetails(request.getDestinationAccountNumber() + "/" + request.getDestinationBankIfsc());
        log.setAmount(request.getAmount());
        log.setCurrency("INR");
        log.setGatewayProvider(request.getPaymentType());
        log.setStatus("PENDING");
        log.setInitiatedAt(LocalDateTime.now());
        
        paymentGatewayLogRepository.save(log);
        
        PaymentGatewayResponseDTO response = new PaymentGatewayResponseDTO();
        response.setReferenceNumber(referenceNumber);
        response.setStatus("PENDING");
        response.setMessage("Payment initiated successfully");
        response.setAmount(request.getAmount());
        response.setCompletedAt(null);
        response.setEstimatedSettlementTime(getSettlementTime(request.getPaymentType()));
        
        return response;
    }

    @Override
    public PaymentGatewayResponseDTO payBill(BillPaymentRequestDTO request, String initiatedBy) {
        Account account = accountRepository.findByAccountNumber(request.getPayerAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getPayerAccountNumber()));
                
        BillMerchant merchant = billMerchantRepository.findByMerchantCode(request.getMerchantCode())
                .orElseThrow(() -> new ResourceNotFoundException("BillMerchant", "code", request.getMerchantCode()));
                
        if (!merchant.isActive()) {
            throw new RuntimeException("Merchant is not active");
        }
        
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        
        PaymentGatewayLog log = new PaymentGatewayLog();
        log.setReferenceNumber("BILL" + System.currentTimeMillis());
        log.setPaymentType("BILL_PAYMENT");
        log.setSourceAccountId(account.getId());
        log.setDestinationDetails(merchant.getMerchantName());
        log.setAmount(request.getAmount());
        log.setCurrency("INR");
        log.setGatewayProvider("BILL_PAYMENT_GATEWAY");
        log.setStatus("SUCCESS");
        log.setInitiatedAt(LocalDateTime.now());
        log.setCompletedAt(LocalDateTime.now());
        
        paymentGatewayLogRepository.save(log);
        
        PaymentGatewayResponseDTO response = new PaymentGatewayResponseDTO();
        response.setReferenceNumber(log.getReferenceNumber());
        response.setStatus("SUCCESS");
        response.setMessage("Bill payment successful");
        response.setAmount(request.getAmount());
        response.setCompletedAt(log.getCompletedAt());
        
        return response;
    }

    @Override
    public PaymentGatewayResponseDTO getPaymentStatus(String referenceNumber) {
        PaymentGatewayLog log = paymentGatewayLogRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentGatewayLog", "referenceNumber", referenceNumber));
                
        PaymentGatewayResponseDTO response = new PaymentGatewayResponseDTO();
        response.setReferenceNumber(log.getReferenceNumber());
        response.setStatus(log.getStatus());
        response.setMessage("Status fetched successfully");
        response.setAmount(log.getAmount());
        response.setCompletedAt(log.getCompletedAt());
        return response;
    }

    @Override
    public Page<PaymentGatewayLog> getPaymentHistory(UUID accountId, Pageable pageable) {
        return paymentGatewayLogRepository.findBySourceAccountId(accountId, pageable);
    }

    @Override
    public List<BillMerchant> getBillMerchants() {
        return billMerchantRepository.findByActive(true);
    }

    @Override
    public List<BillMerchant> getBillMerchantsByCategory(String category) {
        return billMerchantRepository.findByCategory(category);
    }

    @Override
    public PaymentGatewayResponseDTO retryFailedPayment(String referenceNumber) {
        PaymentGatewayLog log = paymentGatewayLogRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentGatewayLog", "referenceNumber", referenceNumber));
                
        if (!"FAILED".equals(log.getStatus())) {
            throw new RuntimeException("Only failed payments can be retried");
        }
        
        if (log.getRetryCount() >= 3) {
            throw new RuntimeException("Maximum retry limit reached");
        }
        
        log.setRetryCount(log.getRetryCount() + 1);
        log.setStatus("PENDING");
        paymentGatewayLogRepository.save(log);
        
        PaymentGatewayResponseDTO response = new PaymentGatewayResponseDTO();
        response.setReferenceNumber(log.getReferenceNumber());
        response.setStatus("PENDING");
        response.setMessage("Payment retry initiated");
        response.setAmount(log.getAmount());
        
        return response;
    }

    @Override
    public PaymentGatewayResponseDTO cancelPayment(String referenceNumber) {
        PaymentGatewayLog log = paymentGatewayLogRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentGatewayLog", "referenceNumber", referenceNumber));
                
        if (!"PENDING".equals(log.getStatus()) && !"INITIATED".equals(log.getStatus())) {
            throw new RuntimeException("Payment cannot be cancelled in its current state");
        }
        
        Account account = accountRepository.findById(log.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", log.getSourceAccountId().toString()));
                
        account.setBalance(account.getBalance().add(log.getAmount()));
        accountRepository.save(account);
        
        log.setStatus("CANCELLED");
        log.setCompletedAt(LocalDateTime.now());
        paymentGatewayLogRepository.save(log);
        
        PaymentGatewayResponseDTO response = new PaymentGatewayResponseDTO();
        response.setReferenceNumber(log.getReferenceNumber());
        response.setStatus("CANCELLED");
        response.setMessage("Payment cancelled successfully");
        response.setAmount(log.getAmount());
        response.setCompletedAt(log.getCompletedAt());
        
        return response;
    }

    private LocalDateTime getSettlementTime(String paymentType) {
        LocalDateTime now = LocalDateTime.now();
        switch (paymentType != null ? paymentType.toUpperCase() : "") {
            case "RTGS":
                return now.toLocalDate().atTime(16, 0);
            case "NEFT":
                return now.plusDays(1).toLocalDate().atTime(10, 0);
            case "IMPS":
                return now.plusMinutes(5);
            default:
                return now.plusHours(1);
        }
    }
}

