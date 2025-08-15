package com.blue.repository;

import com.blue.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Consultas sempre no escopo de empresaId (tenant).
 */
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Endereços institucionais da empresa (clienteId = null)
    List<Endereco> findByEmpresaIdAndClienteIdIsNull(Long empresaId);

    // Endereços de um cliente da empresa
    List<Endereco> findByEmpresaIdAndClienteId(Long empresaId, Long clienteId);

    Optional<Endereco> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<Endereco> findByIdAndEmpresaIdAndClienteId(Long id, Long empresaId, Long clienteId);

    // Principais
    Optional<Endereco> findByEmpresaIdAndClienteIdIsNullAndPrincipalTrue(Long empresaId);
    Optional<Endereco> findByEmpresaIdAndClienteIdAndPrincipalTrue(Long empresaId, Long clienteId);
}
