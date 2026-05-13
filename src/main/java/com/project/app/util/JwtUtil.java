package com.project.app.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil //Handles token generation and parsing
{

	// ✅ FIXED: stable secret key (IMPORTANT)
    private static final String SECRET =
            "my-super-secret-key-my-super-secret-key-12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private final long expirationMs = 86400000; // 1 day

    // Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract Username
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.trim())   // 🔥 FIXED
                .getBody()
                .getSubject();
    }

    // Validate Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.trim()); // 🔥 FIXED
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}