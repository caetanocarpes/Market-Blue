package com.blue.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Classe utilitária para gerar e validar tokens JWT.
 * Agora pega os valores de secret e expiration do application.yml.
 */
@Component
public class JwtUtil {

    // Lê a chave secreta do application.yml
    @Value("${jwt.secret}")
    private String secretKey;

    // Lê o tempo de expiração do token (em milissegundos) do application.yml
    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Gera um token JWT usando o e-mail como "subject".
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Identificação do usuário
                .setIssuedAt(new Date()) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Data de expiração
                .signWith(SignatureAlgorithm.HS256, secretKey) // Assina com a chave secreta
                .compact();
    }

    /**
     * Valida se o token pertence ao usuário e não está expirado.
     */
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Extrai o e-mail (subject) de dentro do token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma informação específica (claim) do token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Verifica se o token já expirou.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Lê todos os dados (claims) do token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
