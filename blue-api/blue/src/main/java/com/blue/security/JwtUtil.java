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

    private Key buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret não configurado no application.yml");
        }
        // 1) tenta Base64
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret.trim());
            if (keyBytes.length < 32) {
                throw new IllegalStateException("security.jwt.secret (Base64) precisa ter >= 32 bytes (256 bits).");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException ignored) { // pega DecodingException e outros
            // 2) não era Base64 → usa bytes da string
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "security.jwt.secret muito curto. Para HS256 use >= 32 bytes (ex.: string com 32+ chars ou Base64 de 32 bytes)."
                );
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String generateToken(String username, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSubject(String token) {
        return getUsernameFromToken(token);
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

    public boolean validateToken(String token, String expectedUsername) {
        String tokenUser = getUsernameFromToken(token);
        return expectedUsername != null && expectedUsername.equals(tokenUser) && !isTokenExpired(token);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails.getUsername());
    }

    private boolean isTokenExpired(String token) {
        Date exp = getClaimFromToken(token, Claims::getExpiration);
        return exp.before(new Date());
    }

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
