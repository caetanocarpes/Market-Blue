package com.blue.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expirationMillis:86400000}") long expirationMs
    ) {
        this.secretKey = buildKey(secret);
        this.expirationMs = expirationMs;
    }

    public long getExpirationMs() { return expirationMs; }

    private Key buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret não configurado");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret.trim());
            if (keyBytes.length < 32) throw new IllegalStateException("JWT secret (Base64) deve ter >= 32 bytes");
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException ignore) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) throw new IllegalStateException("JWT secret muito curto (>=32 chars)");
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

    // ===== Emissão =====
    public String generateToken(String username, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims != null ? claims : Map.of())
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ===== Leitura =====
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getClaimAsString(String token, String claimName) {
        Object v = getAllClaimsFromToken(token).get(claimName);
        return v != null ? String.valueOf(v) : null;
    }

    public Long getClaimAsLong(String token, String claimName) {
        Object v = getAllClaimsFromToken(token).get(claimName);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (NumberFormatException e) { return null; }
    }

    // ===== Validação =====
    public boolean validateToken(String token, UserDetails userDetails) {
        String tokenUser = getUsernameFromToken(token);
        return userDetails != null
                && userDetails.getUsername().equals(tokenUser)
                && !isTokenExpired(token);
    }

    public boolean isValid(String token) {
        try {
            return !isTokenExpired(token) && getAllClaimsFromToken(token) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date exp = getExpirationDate(token);
        return exp.before(new Date());
    }

    // ===== Refresh preservando claims =====
    public String refreshTokenPreservingClaims(String token) {
        Claims old = getAllClaimsFromToken(token);
        Map<String,Object> claims = new HashMap<>(old);
        // Campos padronizados (sub/exp/iat) serão reescritos no builder
        String username = old.getSubject();
        return generateToken(username, claims);
    }

    // ===== Internos =====
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaimsFromToken(token));
    }
}
