package com.chiril.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "LibraryManagementSystemSecretKey256BitsForHMACSHA2024!";
    private static final long EXPIRY_MS = 24L * 60 * 60 * 1000; // 24h

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String memberId, String username, String displayName) {
        return Jwts.builder()
                .subject(memberId)
                .claim("username", username)
                .claim("name", displayName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key)
                .compact();
    }

    public String extractMemberId(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractDisplayName(String token) {
        return parseClaims(token).get("name", String.class);
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
