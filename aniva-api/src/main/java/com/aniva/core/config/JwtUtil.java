package com.aniva.core.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
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
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
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