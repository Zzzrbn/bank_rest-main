package com.example.bankcards.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Value("${encryption.key:defaultEncryptionKey123}")
    private String encryptionKey;

    public String encrypt(String data) {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = new byte[dataBytes.length];
            
            for (int i = 0; i < dataBytes.length; i++) {
                encrypted[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            byte[] decrypted = new byte[encryptedBytes.length];
            
            for (int i = 0; i < encryptedBytes.length; i++) {
                decrypted[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    public String hash(String data) {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            long hash = 0;
            for (byte b : dataBytes) {
                hash = 31 * hash + b;
            }
            return String.valueOf(Math.abs(hash));
        } catch (Exception e) {
            throw new RuntimeException("Error hashing data", e);
        }
    }
}
