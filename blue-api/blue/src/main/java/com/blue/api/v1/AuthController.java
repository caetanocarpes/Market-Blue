package com.blue.api.v1;

import com.blue.domain.Usuario;
import com.blue.dto.UsuarioDTO;
import com.blue.repository.UsuarioRepository;
import com.blue.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller responsável pela autenticação de usuários.
 * Aqui o usuário envia suas credenciais (email/senha) e recebe um token JWT para acessar as rotas protegidas.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Gera o construtor para injeção de dependências
@CrossOrigin(origins = "*") // Libera requisições de qualquer origem (apenas para DEV)
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint para login.
     * Espera um JSON com { "email": "...", "senha": "..." }
     * Retorna: token JWT + dados do usuário (sem expor senha).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String senha = request.get("senha");

        // Busca o usuário pelo e-mail
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        // Verificação de senha (no futuro, usar criptografia com BCrypt)
        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Gera o token JWT
        String token = jwtUtil.generateToken(email);

        // Retorna o token + dados do usuário logado
        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", UsuarioDTO.builder()
                        .id(usuario.getId())
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .role(usuario.getRole())
                        .empresaId(usuario.getEmpresa().getId())
                        .build()
        ));
    }
}
