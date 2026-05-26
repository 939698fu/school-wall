package com.example.schoolwall.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

/**
 * 密码加密工具类
 */
@Slf4j
@Component
public class PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";

    /**
     * 生成随机盐值
     */
    public String generateSalt() {
        Random random = new Random();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     */
    public String encode(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String saltedPassword = password + salt;
            byte[] hash = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword, String salt) {
        String hashedPassword = encode(rawPassword, salt);
        return hashedPassword.equals(encodedPassword);
    }
}
