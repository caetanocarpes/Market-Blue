package com.blue.service;

import com.blue.domain.Empresa;
import com.blue.domain.Usuario;
import com.blue.dto.CadastroEmpresaAdminDTO;
import com.blue.repository.EmpresaRepository;
import com.blue.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fluxo de cadastro inicial (empresa + admin).
 */
@Service
@RequiredArgsConstructor
public class CadastroInicialService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cria uma nova empresa e o usuário admin vinculado (com senha BCrypt).
     */
    @Transactional
    public void cadastrarEmpresaComAdmin(CadastroEmpresaAdminDTO dto) {
        // Evita empresas duplicadas
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }

        // Evita email de admin duplicado
        usuarioRepository.findByEmail(dto.getEmailAdmin()).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail do admin já cadastrado");
        });

        // Cria empresa
        Empresa empresa = Empresa.builder()
                .nome(dto.getNomeEmpresa())
                .email(dto.getEmailEmpresa())
                .telefone(dto.getTelefoneEmpresa())
                .cnpj(dto.getCnpj())
                .build();
        empresaRepository.save(empresa);

        // Cria admin vinculado à empresa (senha com BCrypt)
        Usuario admin = Usuario.builder()
                .nome(dto.getNomeAdmin())
                .email(dto.getEmailAdmin())
                .senha(passwordEncoder.encode(dto.getSenhaAdmin()))
                .role("ADMIN")
                .empresa(empresa)
                .build();

        usuarioRepository.save(admin);
    }
}
