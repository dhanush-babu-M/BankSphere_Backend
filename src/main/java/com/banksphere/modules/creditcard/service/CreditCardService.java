package com.banksphere.modules.creditcard.service;

import com.banksphere.modules.creditcard.dto.request.CreditCardApplicationDTO;
import com.banksphere.modules.creditcard.dto.request.CreditCardPaymentDTO;
import com.banksphere.modules.creditcard.dto.response.CreditCardResponseDTO;
import com.banksphere.modules.creditcard.dto.response.CreditCardStatementDTO;
import com.banksphere.modules.creditcard.entity.CreditCardBill;
import com.banksphere.modules.creditcard.entity.CreditCardTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreditCardService {
    CreditCardResponseDTO applyCreditCard(CreditCardApplicationDTO request);
    CreditCardResponseDTO getCreditCard(UUID id);
    List<CreditCardResponseDTO> getCustomerCreditCards(UUID customerId);
    void makeCreditCardPayment(CreditCardPaymentDTO request);
    CreditCardStatementDTO getStatement(UUID creditCardId);
    void blockCard(UUID id, String reason);
    void unblockCard(UUID id);
    void updateLimit(UUID id, BigDecimal limit);
    Page<CreditCardTransaction> getTransactions(UUID creditCardId, Pageable pageable);
    CreditCardBill generateBill(UUID creditCardId);
}
