package com.blue.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Lê claims do JWT já validado, para acesso em controllers/services. */
@Component
@RequiredArgsConstructor
public class JwtTenant {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    private String bearer() {
        String h = request.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }

    public Long getEmpresaId() {
        String t = bearer();
        return t == null ? null : jwtUtil.getClaimAsLong(t, "empresaId");
    }

    public Long getUserId() {
        String t = bearer();
        return t == null ? null : jwtUtil.getClaimAsLong(t, "userId");
    }

    public Long getClienteId() {
        String t = bearer();
        return t == null ? null : jwtUtil.getClaimAsLong(t, "clienteId");
    }

    public String getRole() {
        String t = bearer();
        return t == null ? null : jwtUtil.getClaimAsString(t, "role");
    }

    public boolean hasRole(String role) {
        String r = getRole();
        return r != null && r.equalsIgnoreCase(role);
    }
}
