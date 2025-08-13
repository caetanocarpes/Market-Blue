package com.blue.service;

import com.blue.domain.Empresa;
import com.blue.domain.Usuario;
import com.blue.dto.CadastroEmpresaAdminDTO;
import com.blue.repository.EmpresaRepository;
import com.blue.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastroInicialService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria uma nova empresa e o usuário admin vinculado.
     */
    public void cadastrarEmpresaComAdmin(CadastroEmpresaAdminDTO dto) {
        // Evita empresas duplicadas
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }

        // Cria empresa
        Empresa empresa = Empresa.builder()
                .nome(dto.getNomeEmpresa())
                .email(dto.getEmailEmpresa())
                .telefone(dto.getTelefoneEmpresa())
                .cnpj(dto.getCnpj())
                .build();
        empresaRepository.save(empresa);

        // Cria admin vinculado à empresa
        Usuario admin = Usuario.builder()
                .nome(dto.getNomeAdmin())
                .email(dto.getEmailAdmin())
                .senha(dto.getSenhaAdmin()) // ⚠ Futuro: criptografar
                .role("ADMIN")
                .empresa(empresa)
                .build();
        usuarioRepository.save(admin);
    }
}
