package com.blue.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String uri = request.getRequestURI();
        final String method = request.getMethod();

        // --- Fast-path: rotas públicas (mantém em sincronia com SecurityConfig) ---
        final boolean isPublic =
                "OPTIONS".equalsIgnoreCase(method) ||
                        ("GET".equalsIgnoreCase(method) && (
                                "/".equals(uri) ||
                                        "/index.html".equals(uri) ||
                                        "/login.html".equals(uri) ||
                                        "/cadastro.html".equals(uri) ||
                                        uri.startsWith("/css/") ||
                                        uri.startsWith("/js/") ||
                                        uri.startsWith("/images/") ||
                                        uri.startsWith("/assets/") ||
                                        "/favicon.ico".equals(uri)
                        )) ||
                        uri.startsWith("/api/auth/") ||
                        ("POST".equalsIgnoreCase(method) && ("/api/empresas".equals(uri) || "/api/usuarios".equals(uri)));

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- JWT apenas quando houver Bearer ---
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            // Token inválido/expirado: segue sem autenticar (Security decidirá -> 401)
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
