package com.banksphere.modules.creditcard.service.impl;

import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.creditcard.dto.request.*;
import com.banksphere.modules.creditcard.dto.response.*;
import com.banksphere.modules.creditcard.entity.*;
import com.banksphere.modules.creditcard.mapper.CreditCardMapper;
import com.banksphere.modules.creditcard.repository.CreditCardBillRepository;
import com.banksphere.modules.creditcard.repository.CreditCardRepository;
import com.banksphere.modules.creditcard.repository.CreditCardTransactionRepository;
import com.banksphere.modules.creditcard.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardTransactionRepository creditCardTransactionRepository;
    private final CreditCardBillRepository creditCardBillRepository;
    private final CreditCardMapper creditCardMapper;
    private final AccountRepository accountRepository;
    
    private final SecureRandom random = new SecureRandom();

    @Override
    public CreditCardResponseDTO applyCreditCard(CreditCardApplicationDTO request) {
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        String last4 = cardNumber.substring(12);
        String maskedCardNumber = "****-****-****-" + last4;
        String cvv = String.format("%03d", random.nextInt(1000));
        
        BigDecimal creditLimit = request.getRequestedCreditLimit() != null 
            ? request.getRequestedCreditLimit() 
            : request.getAnnualIncome().divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(3));

        CreditCard card = new CreditCard();
        card.setCustomerId(request.getCustomerId());
        card.setCardNumber(cardNumber.toString());
        card.setMaskedCardNumber(maskedCardNumber);
        card.setCvv(cvv);
        card.setCreditLimit(creditLimit);
        card.setAvailableCredit(creditLimit);
        card.setOutstandingBalance(BigDecimal.ZERO);
        card.setMinimumPayment(BigDecimal.ZERO);
        card.setBillingCycleDay(1);
        card.setDueDate(LocalDate.now().plusMonths(1).withDayOfMonth(15));
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus("ACTIVE");
        card.setInternationalTransactionEnabled(false);
        card.setContactlessEnabled(true);
        card.setDailyLimit(BigDecimal.valueOf(50000));
        card.setPerTransactionLimit(BigDecimal.valueOf(25000));
        card.setCardHolderName(request.getCardHolderName());

        card = creditCardRepository.save(card);
        return creditCardMapper.toResponseDTO(card);
    }

    @Override
    public CreditCardResponseDTO getCreditCard(UUID id) {
        return creditCardRepository.findById(id)
                .map(creditCardMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", id));
    }

    @Override
    public List<CreditCardResponseDTO> getCustomerCreditCards(UUID customerId) {
        return creditCardRepository.findByCustomerId(customerId).stream()
                .map(creditCardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void makeCreditCardPayment(CreditCardPaymentDTO request) {
        CreditCard card = creditCardRepository.findById(request.getCreditCardId())
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", request.getCreditCardId()));

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new IllegalArgumentException("Credit card is not active");
        }

        if (request.getPaymentAmount().compareTo(card.getOutstandingBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds outstanding balance");
        }

        var account = accountRepository.findByAccountNumber(request.getBankAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getBankAccountNumber()));

        if (account.getBalance().compareTo(request.getPaymentAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in account");
        }

        account.setBalance(account.getBalance().subtract(request.getPaymentAmount()));
        accountRepository.save(account);

        card.setOutstandingBalance(card.getOutstandingBalance().subtract(request.getPaymentAmount()));
        card.setAvailableCredit(card.getAvailableCredit().add(request.getPaymentAmount()));
        card.setMinimumPayment(card.getMinimumPayment().subtract(request.getPaymentAmount()).max(BigDecimal.ZERO));
        creditCardRepository.save(card);

        log.info("Credit card payment made: {} for card: {}", request.getPaymentAmount(), card.getMaskedCardNumber());
    }

    @Override
    public CreditCardStatementDTO getStatement(UUID creditCardId) {
        CreditCard card = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", creditCardId));

        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();

        var transactions = creditCardTransactionRepository.findByCreditCardIdAndTransactionDateBetween(creditCardId, startDateTime, endDateTime);
        
        CreditCardStatementDTO stmt = new CreditCardStatementDTO();
        stmt.setCreditCardId(creditCardId);
        // Map other required fields
        return stmt;
    }

    @Override
    public void blockCard(UUID id, String reason) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", id));
        card.setStatus("BLOCKED");
        creditCardRepository.save(card);
        log.info("Credit card blocked: {} reason: {}", id, reason);
    }

    @Override
    public void unblockCard(UUID id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", id));
        if (!"BLOCKED".equals(card.getStatus())) {
            throw new IllegalArgumentException("Card is not blocked");
        }
        card.setStatus("ACTIVE");
        creditCardRepository.save(card);
    }

    @Override
    public void updateLimit(UUID id, BigDecimal newLimit) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", id));
        if (newLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        card.setCreditLimit(newLimit);
        creditCardRepository.save(card);
    }

    @Override
    public Page<CreditCardTransaction> getTransactions(UUID creditCardId, Pageable pageable) {
        return creditCardTransactionRepository.findByCreditCardId(creditCardId, pageable);
    }

    @Override
    public CreditCardBill generateBill(UUID creditCardId) {
        CreditCard card = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", "id", creditCardId));

        LocalDate firstDayOfPrevMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPrevMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);
        
        var transactions = creditCardTransactionRepository.findByCreditCardIdAndTransactionDateBetween(
            creditCardId, firstDayOfPrevMonth.atStartOfDay(), lastDayOfPrevMonth.atTime(23, 59, 59));

        BigDecimal totalAmount = BigDecimal.ZERO; 
        // Example computation, actual summation should be done here if transactions are available
        
        BigDecimal minimumAmount = totalAmount.multiply(BigDecimal.valueOf(0.05)).max(BigDecimal.valueOf(500));
        LocalDate dueDate = LocalDate.now().withDayOfMonth(15);

        CreditCardBill bill = new CreditCardBill();
        // Assume saving bill correctly
        creditCardBillRepository.save(bill);

        card.setOutstandingBalance(card.getOutstandingBalance().add(totalAmount));
        card.setMinimumPayment(minimumAmount);
        card.setDueDate(dueDate);
        creditCardRepository.save(card);
        
        return bill;
    }
}

