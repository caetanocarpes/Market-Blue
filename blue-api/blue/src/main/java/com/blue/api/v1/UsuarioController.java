package com.blue.api.admin;

import com.blue.dto.UsuarioDTO;
import com.blue.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller para gerenciar usuários da empresa do admin logado.
 */
@RestController
@RequestMapping("/api/admin/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** Lista todos os usuários da empresa do admin logado. */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarMeusUsuarios() {
        return ResponseEntity.ok(usuarioService.listarDaMinhaEmpresa());
    }

    /** Busca um usuário específico da empresa do admin logado. */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /** Cria um novo usuário na empresa do admin logado. */
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@RequestBody @Valid UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.criar(dto));
    }

    /** Atualiza um usuário da empresa do admin logado. */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    /** Remove um usuário da empresa do admin logado. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
