package com.banksphere.core.notification.sms;

import com.banksphere.core.notification.sms.dto.SmsRequestDTO;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface SmsService {
    void sendSms(SmsRequestDTO request);
    void sendOtpSms(String mobile, String otp);
    void sendTransactionAlertSms(String mobile, BigDecimal amount, String txnType, String accountSuffix);
    void sendLoginAlertSms(String mobile, String ipAddress, String deviceInfo);
    void sendLoanEmiAlertSms(String mobile, BigDecimal emiAmount, LocalDate dueDate);
}
