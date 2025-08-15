package com.blue.repository;

import com.blue.domain.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findAllByEmpresaId(Long empresaId, Pageable pageable);
    List<Categoria> findAllByEmpresaId(Long empresaId);

    Optional<Categoria> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<Categoria> findByEmpresaIdAndNomeIgnoreCase(Long empresaId, String nome);
    boolean existsByEmpresaIdAndNomeIgnoreCase(Long empresaId, String nome);
}
