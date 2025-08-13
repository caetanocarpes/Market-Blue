package com.blue.service;

import com.blue.domain.Categoria;
import com.blue.dto.CategoriaDTO;
import com.blue.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Camada de regras de negócio para Categoria.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    public List<Categoria> listar() {
        return repo.findAll();
    }

    public Categoria criar(CategoriaDTO dto) {
        Categoria cat = Categoria.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .build();
        return repo.save(cat);
    }
}
