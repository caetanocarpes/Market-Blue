package com.blue.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTenant {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    private String extrairBearer() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
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
        return token == null ? null : jwtUtil.getClaimAsString(token, "role");
    }

    public String getSubject() {
        String token = extrairBearer();
        return token == null ? null : jwtUtil.getUsernameFromToken(token);
    }
}
