package com.blue.repository;

import com.blue.domain.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByEmail(String email);
    boolean existsByCnpj(String cnpj);
}
