package com.example.backend.service;

import com.example.backend.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expirationMs
    ) {
        this.signingKey = buildSigningKey(secret);
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public Role extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(role);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey buildSigningKey(String secret) {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        try {
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException ex) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256", ex);
        }
    }

}
