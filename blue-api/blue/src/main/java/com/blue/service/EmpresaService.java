package com.blue.service;

import com.blue.domain.Empresa;
import com.blue.dto.EmpresaDTO;
import com.blue.repository.EmpresaRepository;
import com.blue.security.UsuarioAutenticadoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócios relacionada às empresas.
 */
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioAutenticadoUtil usuarioAutenticadoUtil; // Utilitário para pegar dados do usuário logado

    /**
     * Lista todas as empresas (apenas se for necessário em contexto admin global).
     * Se quiser restringir, pode filtrar pelo usuário logado.
     */
    public List<EmpresaDTO> listar() {
        return empresaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna os dados da empresa do usuário logado.
     */
    public EmpresaDTO buscarMinhaEmpresa() {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        return toDTO(empresa);
    }

    /**
     * Cria uma nova empresa (usado apenas no cadastro inicial).
     */
    public EmpresaDTO criar(EmpresaDTO dto) {
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }
        Empresa empresa = toEntity(dto);
        return toDTO(empresaRepository.save(empresa));
    }

    /**
     * Atualiza os dados da empresa do usuário logado.
     */
    public EmpresaDTO atualizarMinhaEmpresa(EmpresaDTO dto) {
        Long empresaId = usuarioAutenticadoUtil.getEmpresaIdUsuario();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        empresa.setNome(dto.getNome());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setCnpj(dto.getCnpj());

        return toDTO(empresaRepository.save(empresa));
    }

    /**
     * Converte entidade para DTO.
     */
    private EmpresaDTO toDTO(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .nome(empresa.getNome())
                .email(empresa.getEmail())
                .telefone(empresa.getTelefone())
                .cnpj(empresa.getCnpj())
                .build();
    }

    /**
     * Converte DTO para entidade.
     */
    private Empresa toEntity(EmpresaDTO dto) {
        return Empresa.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .cnpj(dto.getCnpj())
                .build();
    }
}
