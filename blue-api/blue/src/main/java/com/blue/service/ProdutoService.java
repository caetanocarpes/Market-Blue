package com.blue.service;

import com.blue.domain.Categoria;
import com.blue.domain.Produto;
import com.blue.dto.ProdutoDTO;
import com.blue.repository.CategoriaRepository;
import com.blue.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Camada de regras de negócio para Produto.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final CategoriaRepository categoriaRepo;

    public ProdutoService(ProdutoRepository produtoRepo, CategoriaRepository categoriaRepo) {
        this.produtoRepo = produtoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public List<Produto> listar() {
        return produtoRepo.findAll();
    }

    public Produto criar(ProdutoDTO dto) {
        Categoria categoria = categoriaRepo.findById(dto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Produto p = Produto.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .preco(dto.preco())
                .estoque(dto.estoque())
                .categoria(categoria)
                .build();

        return produtoRepo.save(p);
    }
}
