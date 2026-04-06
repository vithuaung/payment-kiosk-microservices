package com.conversion.pmk.gateway.security;

import com.conversion.pmk.common.exception.PmkException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT creation and validation utilities
@Slf4j
@Component
public class JwtUtil {

    @Value("${pmk.jwt.secret}")
    private String secret;

    @Value("${pmk.jwt.expiry-ms:3600000}")
    private long expiryMs;

    // Derive a signing key from the configured secret
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Create an HS256 JWT for the given terminal code and role
    public String generateToken(String terminalCode, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(terminalCode)
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(signingKey())
                .compact();
    }

    // Parse and return claims; throws PmkException if the token is invalid or expired
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            throw new PmkException("Invalid token", "AUTH_INVALID_TOKEN", ex);
        }
    }

    // Extract the subject (terminal code) without full claims
    public String extractSubject(String token) {
        return validateToken(token).getSubject();
    }
}
