package com.banksphere.core.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HMACGeneratorUtil {

    @Value("${banksphere.encryption.aes-key:DefaultSecretKey}")
    private String secretKey;

    public String generateHMAC(String data) {
        // TODO: HmacSHA256, Base64 encoded
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean verifyHMAC(String data, String hmac) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String generateTransactionSignature(String transactionId, BigDecimal amount, String accountNumber) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
