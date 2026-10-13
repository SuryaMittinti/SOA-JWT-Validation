package com.example.jwtvalidation.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey signingKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes for HS256.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, Object> validateAndExtractClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Token is missing.");
        }
        String compactToken = token.trim();
        if (compactToken.regionMatches(true, 0, "Bearer ", 0, 7)) {
            compactToken = compactToken.substring(7).trim();
        }

        Claims parsedClaims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(compactToken)
                .getPayload();

        // JJWT checks signature and expiration (when the exp claim is present).
        // This project requires exp so tokens cannot be valid indefinitely.
        Date expiration = parsedClaims.getExpiration();
        if (expiration == null) {
            throw new JwtException("Token must contain an expiration (exp) claim.");
        }
        if (!expiration.after(new Date())) {
            throw new JwtException("Token has expired.");
        }

        return new LinkedHashMap<>(parsedClaims);
    }

    public Map<String, Object> createDemoToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 15 * 60 * 1000);
        String token = Jwts.builder()
                .subject(subject)
                .claim("role", "USER")
                .claim("scope", "read")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
        return Map.of("token", token, "expiresInSeconds", 900);
    }
}
