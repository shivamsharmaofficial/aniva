package com.aniva.core.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final int MINIMUM_JWT_SECRET_BYTES = 32;

    private final String secret;
    private final long expirationTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationTime) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be configured");
        }

        if (secret.getBytes(StandardCharsets.UTF_8).length < MINIMUM_JWT_SECRET_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
        }

        this.secret = secret;
        this.expirationTime = expirationTime;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /*
     ===============================
     GENERATE TOKEN
     ===============================
    */

    public String generateToken(String email, List<String> roles) {

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expirationTime)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     ===============================
     EXTRACT EMAIL
     ===============================
    */

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
     ===============================
     EXTRACT ROLES
     ===============================
    */

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    /*
     ===============================
     VALIDATE TOKEN
     ===============================
    */

    public boolean validateToken(String token) {

        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /*
     ===============================
     TOKEN EXPIRY CHECK
     ===============================
    */

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /*
     ===============================
     PARSE CLAIMS
     ===============================
    */

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
