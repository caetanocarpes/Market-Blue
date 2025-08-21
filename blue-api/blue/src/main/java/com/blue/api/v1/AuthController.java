package com.blue.api.v1;

import com.blue.domain.Usuario;
import com.blue.dto.UsuarioDTO;
import com.blue.repository.UsuarioRepository;
import com.blue.security.JwtTenant;
import com.blue.security.JwtUtil;
import com.blue.security.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtTenant jwtTenant;

    // ===== DTOs =====
    public record LoginRequest(@NotBlank @Email String email,
                               @NotBlank String senha) {}

    public record LoginResponse(String token,
                                String tokenType,
                                long   expiresAt,
                                UsuarioDTO usuario) {}

    public record RefreshRequest(String token) {} // opcional: pode mandar no body

    public record TokenResponse(String token,
                                String tokenType,
                                long   expiresAt) {}

    // ===== /login =====
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Usuario u = usuarioRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(req.senha(), u.getSenha())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", u.getId());
        claims.put("empresaId", u.getEmpresa() != null ? u.getEmpresa().getId() : null);
        claims.put("role", u.getRole());
        if (Roles.CLIENTE.equalsIgnoreCase(u.getRole()) && u.getClienteId() != null) {
            claims.put("clienteId", u.getClienteId());
        }

        String token = jwtUtil.generateToken(u.getEmail(), claims);
        long   exp   = System.currentTimeMillis() + jwtUtil.getExpirationMs();

        UsuarioDTO dto = UsuarioDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .role(u.getRole())
                .empresaId(u.getEmpresa() != null ? u.getEmpresa().getId() : null)
                .build();

        return ResponseEntity.ok(new LoginResponse(token, "Bearer", exp, dto));
    }

    // ===== /refresh =====
    // Aceita o token no Authorization: Bearer ... OU no body { "token": "..." }
    @PostMapping(path = "/refresh", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponse> refresh(@RequestBody(required = false) RefreshRequest body,
                                                 @RequestHeader(value = "Authorization", required = false) String auth) {
        String token = extractBearer(auth);
        if (token == null && body != null) token = body.token();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Token ausente");
        }

        // valida assinatura e expiração
        if (!jwtUtil.isValid(token)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Token inválido ou expirado");
        }

        String refreshed = jwtUtil.refreshTokenPreservingClaims(token);
        long   exp       = System.currentTimeMillis() + jwtUtil.getExpirationMs();
        return ResponseEntity.ok(new TokenResponse(refreshed, "Bearer", exp));
    }

    // ===== /me =====
    // Retorna os dados do usuário baseados no token (útil pro front)
    @GetMapping(path = "/me", produces = "application/json")
    public ResponseEntity<Map<String,Object>> me() {
        Map<String,Object> out = new HashMap<>();
        out.put("userId", jwtTenant.getUserId());
        out.put("empresaId", jwtTenant.getEmpresaId());
        out.put("clienteId", jwtTenant.getClienteId());
        out.put("role", jwtTenant.getRole());
        return ResponseEntity.ok(out);
    }

    private String extractBearer(String header) {
        if (header != null && header.startsWith("Bearer ")) return header.substring(7);
        return null;
    }
}
