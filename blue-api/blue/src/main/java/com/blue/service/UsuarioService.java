package com.blue.service;

import com.blue.domain.Usuario;
import com.blue.dto.UsuarioDTO;
import com.blue.repository.UsuarioRepository;
import com.blue.repository.EmpresaRepository;
import com.blue.security.UsuarioAutenticadoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócios relacionada aos usuários.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioAutenticadoUtil usuarioAutenticadoUtil; // Utilitário para pegar o usuário logado

    /**
     * Lista todos os usuários vinculados à empresa do usuário logado.
     * Garante que um admin só veja usuários da própria empresa.
     */
    public List<UsuarioDTO> listarDaMinhaEmpresa() {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        return usuarioRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário específico da empresa do usuário logado.
     */
    public UsuarioDTO buscarPorId(Long id) {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresa().getId().equals(empresaId)) // garante que é da mesma empresa
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado na sua empresa"));
        return toDTO(usuario);
    }

    /**
     * Cria um novo usuário vinculado à empresa do usuário logado.
     */
    public UsuarioDTO criar(UsuarioDTO dto) {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        var empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        Usuario usuario = toEntity(dto);
        usuario.setEmpresa(empresa); // vincula à empresa do admin logado

        return toDTO(usuarioRepository.save(usuario));
    }

    /**
     * Atualiza os dados de um usuário da mesma empresa.
     */
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado na sua empresa"));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setRole(dto.getRole());

        return toDTO(usuarioRepository.save(usuario));
    }

    /**
     * Converte entidade para DTO (não expõe senha).
     */
    private UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .empresaId(usuario.getEmpresa().getId())
                .build();
    }

    /**
     * Converte DTO para entidade.
     * A senha deve ser tratada em outra camada (criptografia).
     */
    private Usuario toEntity(UsuarioDTO dto) {
        return Usuario.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }
}
