package com.blue.service;

import com.blue.domain.Empresa;
import com.blue.dto.EmpresaDTO;
import com.blue.repository.EmpresaRepository;
import com.blue.security.JwtTenant;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócios relacionada às empresas.
 */
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final JwtTenant tenant;

    /** Lista todas as empresas (apenas para administração global, se necessário). */
    @Transactional(readOnly = true)
    public List<EmpresaDTO> listar() {
        return empresaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Retorna os dados da empresa do usuário logado (empresaId do JWT). */
    @Transactional(readOnly = true)
    public EmpresaDTO buscarMinhaEmpresa() {
        Long empresaId = requireEmpresaId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        return toDTO(empresa);
    }

    /** Cria uma nova empresa (usado apenas no cadastro inicial). */
    @Transactional
    public EmpresaDTO criar(EmpresaDTO dto) {
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }
        Empresa empresa = toEntity(dto);
        return toDTO(empresaRepository.save(empresa));
    }

    /** Atualiza os dados da empresa do usuário logado. */
    @Transactional
    public EmpresaDTO atualizarMinhaEmpresa(EmpresaDTO dto) {
        Long empresaId = requireEmpresaId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        if (dto.getNome() != null)     empresa.setNome(dto.getNome());
        if (dto.getEmail() != null)    empresa.setEmail(dto.getEmail());
        if (dto.getTelefone() != null) empresa.setTelefone(dto.getTelefone());
        if (dto.getCnpj() != null)     empresa.setCnpj(dto.getCnpj());

        return toDTO(empresaRepository.save(empresa));
    }

    // ================= helpers =================

    private EmpresaDTO toDTO(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .nome(empresa.getNome())
                .email(empresa.getEmail())
                .telefone(empresa.getTelefone())
                .cnpj(empresa.getCnpj())
                .build();
    }

    private Empresa toEntity(EmpresaDTO dto) {
        return Empresa.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .cnpj(dto.getCnpj())
                .build();
    }

    private Long requireEmpresaId() {
        Long empresaId = tenant.getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("empresaId ausente no token JWT");
        return empresaId;
    }
}
