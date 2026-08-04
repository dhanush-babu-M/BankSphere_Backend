package com.banksphere.core.notification.email;

import com.banksphere.core.notification.email.dto.EmailRequestDTO;
import java.math.BigDecimal;

public interface EmailService {
    void sendEmail(EmailRequestDTO request);
    void sendOtp(String to, String customerName, String otp);
    void sendTransactionAlert(String to, String customerName, String accountNumber, BigDecimal amount, String txnType, String referenceNumber);
    void sendWelcomeEmail(String to, String customerName, String accountNumber, String customerId);
    void sendPasswordResetEmail(String to, String customerName, String resetLink);
    void sendLoanApprovalEmail(String to, String customerName, BigDecimal loanAmount, String loanId);
}
