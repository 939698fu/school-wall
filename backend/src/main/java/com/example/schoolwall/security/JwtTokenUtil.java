package com.example.schoolwall.security;

import com.example.schoolwall.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username, String nickname, String avatar) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("nickname", nickname);
        claims.put("avatar", avatar);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtConfig.getExpiration());

        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
        }
        return null;
    }

    /**
     * 验证Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims != null) {
                return claims.getExpiration().before(new Date());
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 从Token中获取昵称
     */
    public String getNicknameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("nickname", String.class);
        }
        return null;
    }

    /**
     * 从Token中获取头像
     */
    public String getAvatarFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("avatar", String.class);
        }
        return null;
    }

    /**
     * 从Token获取当前用户信息
     */
    public CurrentUser getCurrentUser(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        CurrentUser user = new CurrentUser();
        Object userId = claims.get("userId");
        if (userId instanceof Long) {
            user.setUserId((Long) userId);
        } else if (userId instanceof Integer) {
            user.setUserId(((Integer) userId).longValue());
        }
        user.setUsername(claims.get("username", String.class));
        user.setNickname(claims.get("nickname", String.class));
        user.setAvatar(claims.get("avatar", String.class));
        return user;
    }
}
