package com.banksphere.core.notification.sms.impl;

import com.banksphere.core.notification.sms.SmsService;
import com.banksphere.core.notification.sms.dto.SmsRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    @Async
    @Override
    public void sendSms(SmsRequestDTO request) {
        log.info("[SMS-STUB] Sending SMS to: {} Message: {}", request.getTo(), request.getMessage());
        // TODO: throw new UnsupportedOperationException("Not yet implemented");
    }

    @Async
    @Override
    public void sendOtpSms(String mobile, String otp) {
        log.info("[SMS-STUB] Sending OTP SMS to: {} OTP: {}", mobile, otp);
        // TODO: throw new UnsupportedOperationException("Not yet implemented");
    }

    @Async
    @Override
    public void sendTransactionAlertSms(String mobile, BigDecimal amount, String txnType, String accountSuffix) {
        log.info("[SMS-STUB] Sending Transaction Alert SMS to: {} Amount: {} Type: {} Account: {}", mobile, amount, txnType, accountSuffix);
        // TODO: throw new UnsupportedOperationException("Not yet implemented");
    }

    @Async
    @Override
    public void sendLoginAlertSms(String mobile, String ipAddress, String deviceInfo) {
        log.info("[SMS-STUB] Sending Login Alert SMS to: {} IP: {} Device: {}", mobile, ipAddress, deviceInfo);
        // TODO: throw new UnsupportedOperationException("Not yet implemented");
    }

    @Async
    @Override
    public void sendLoanEmiAlertSms(String mobile, BigDecimal emiAmount, LocalDate dueDate) {
        log.info("[SMS-STUB] Sending Loan EMI Alert SMS to: {} Amount: {} Due Date: {}", mobile, emiAmount, dueDate);
        // TODO: throw new UnsupportedOperationException("Not yet implemented");
    }
}
