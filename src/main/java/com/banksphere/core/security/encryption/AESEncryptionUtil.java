package com.banksphere.core.security.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESEncryptionUtil {

    @Value("${banksphere.encryption.aes-key:DefaultSuperSecretKeyForAES256!}")
    private String aesKey;

    public String encrypt(String plainText) {
        // TODO: implement AES/CBC/PKCS5Padding encryption, Base64 encode result, IV prepended
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String decrypt(String cipherText) {
        // TODO: implement decryption, reverse of above
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private SecretKeySpec getSecretKeySpec() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
