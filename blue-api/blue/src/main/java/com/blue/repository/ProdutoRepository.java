package com.blue.repository;

import com.blue.domain.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Page<Produto> findAllByEmpresaId(Long empresaId, Pageable pageable);
    List<Produto> findAllByEmpresaId(Long empresaId);

    Optional<Produto> findByIdAndEmpresaId(Long id, Long empresaId);

    Page<Produto> findByEmpresaIdAndNomeContainingIgnoreCase(Long empresaId, String nome, Pageable pageable);
    Page<Produto> findByEmpresaIdAndCategoriaId(Long empresaId, Long categoriaId, Pageable pageable);

    boolean existsByEmpresaIdAndNomeIgnoreCase(Long empresaId, String nome);
}
