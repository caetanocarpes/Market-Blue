package com.blue.api.v1;

import com.blue.domain.Usuario;
import com.blue.dto.UsuarioDTO;
import com.blue.repository.UsuarioRepository;
import com.blue.security.JwtUtil;
import com.blue.security.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String senha = request.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("empresaId", usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null);
        claims.put("role", usuario.getRole());
        if (Roles.CLIENTE.equalsIgnoreCase(usuario.getRole()) && usuario.getClienteId() != null) {
            claims.put("clienteId", usuario.getClienteId());
        }

        String token = jwtUtil.generateToken(email, claims);

        UsuarioDTO dto = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .empresaId(usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null)
                .build();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", dto
        ));
    }
}
