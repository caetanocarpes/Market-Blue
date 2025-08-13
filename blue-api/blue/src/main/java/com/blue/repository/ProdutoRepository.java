package com.blue.repository;

import com.blue.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface que gerencia operações no banco para Produto.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
