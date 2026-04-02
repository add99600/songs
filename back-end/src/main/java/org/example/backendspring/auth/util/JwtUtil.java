package org.example.backendspring.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성, 검증, 파싱 유틸리티.
 */
@Component
public class JwtUtil {

    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;

    // HMAC-SHA 서명 키를 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 사용자 ID를 subject로 하여 JWT를 생성한다.
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWT 토큰의 유효성을 검증한다.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * JWT에서 사용자 ID를 추출한다.
     */
    public Long getUserId(String token) {
        String subject = parseClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    // 토큰을 파싱하여 Claims를 반환
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
