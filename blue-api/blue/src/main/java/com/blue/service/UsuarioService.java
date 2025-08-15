package com.blue.service;

import com.blue.domain.Empresa;
import com.blue.domain.Usuario;
import com.blue.dto.UsuarioDTO;
import com.blue.repository.EmpresaRepository;
import com.blue.repository.UsuarioRepository;
import com.blue.security.JwtTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de Usuário (Admin) com escopo por empresa (tenant).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final JwtTenant tenant;
    private final PasswordEncoder passwordEncoder;

    /** Lista usuários do tenant atual. */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarDaMinhaEmpresa() {
        Long empresaId = requireEmpresaId();
        return usuarioRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Busca um usuário do tenant atual por ID. */
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Long empresaId = requireEmpresaId();
        Usuario u = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado nesta empresa."));
        return toDto(u);
    }

    /**
     * Cria um usuário no tenant atual.
     * OBS: Como o DTO não possui senha, definimos uma senha TEMPORÁRIA "123456"
     * (criptografada). Depois crie um fluxo de "reset de senha".
     */
    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto) {
        Long empresaId = requireEmpresaId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada"));

        usuarioRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        });

        Usuario novo = new Usuario();
        novo.setNome(dto.getNome());
        novo.setEmail(dto.getEmail());
        novo.setRole(dto.getRole());
        novo.setEmpresa(empresa);

        // Senha temporária padrão — sempre criptografada
        novo.setSenha(passwordEncoder.encode("123456"));

        Usuario salvo = usuarioRepository.save(novo);
        return toDto(salvo);
    }

    /** Atualiza um usuário do tenant atual. */
    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Long empresaId = requireEmpresaId();
        Usuario u = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado nesta empresa."));

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(other -> {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            });
            u.setEmail(dto.getEmail());
        }
        if (dto.getNome() != null)  u.setNome(dto.getNome());
        if (dto.getRole() != null)  u.setRole(dto.getRole());

        Usuario salvo = usuarioRepository.save(u);
        return toDto(salvo);
    }

    /** Remove um usuário do tenant atual. */
    @Transactional
    public void deletar(Long id) {
        Long empresaId = requireEmpresaId();
        Usuario u = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado nesta empresa."));
        usuarioRepository.delete(u);
    }

    // ================= helpers =================

    private Long requireEmpresaId() {
        Long empresaId = tenant.getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("empresaId ausente no token JWT");
        return empresaId;
    }

    private UsuarioDTO toDto(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .role(u.getRole())
                .empresaId(u.getEmpresa() != null ? u.getEmpresa().getId() : null)
                .build();
    }
}
