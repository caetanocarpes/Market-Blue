package com.blue.security;

import com.blue.domain.Usuario;
import com.blue.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta todas as requisições e valida o token JWT.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        // Extrai o token do header (formato: "Bearer token")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            email = jwtUtil.extractEmail(token);
        }

        // Se o token for válido e o usuário ainda não estiver autenticado
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null && jwtUtil.validateToken(token, email)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, null);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define o usuário como autenticado no contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // Continua a requisição
    }
}
