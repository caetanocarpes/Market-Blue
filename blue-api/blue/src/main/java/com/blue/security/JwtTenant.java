package com.blue.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Lê os claims do JWT (já validados pelo JwtFilter/JwtUtil) e expõe:
 * empresaId, userId, clienteId e role do usuário logado.
 */
@Component
@RequiredArgsConstructor
public class JwtTenant {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    /**
     * Extrai o token do cabeçalho Authorization usando o esquema Bearer.
     */
    private String extrairBearer() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public Long getEmpresaId() {
        String token = extrairBearer();
        return token == null ? null : jwtUtil.getClaimAsLong(token, "empresaId");
    }

    public Long getUserId() {
        String token = extrairBearer();
        return token == null ? null : jwtUtil.getClaimAsLong(token, "userId");
    }

    public Long getClienteId() {
        String token = extrairBearer();
        return token == null ? null : jwtUtil.getClaimAsLong(token, "clienteId");
    }

    public String getRole() {
        String token = extrairBearer();
        // Usa o helper público do JwtUtil, evitando acessar métodos privados.
        return token == null ? null : jwtUtil.getClaimAsString(token, "role");
    }
}
