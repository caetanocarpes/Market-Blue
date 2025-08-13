package com.blue.api.v1;

import com.blue.domain.Produto;
import com.blue.dto.ProdutoDTO;
import com.blue.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints da API pública para Produtos.
 */
@RestController
@RequestMapping("/api/v1/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar() {
        return service.listar();
    }

    @PostMapping
    public Produto criar(@RequestBody @Valid ProdutoDTO dto) {
        return service.criar(dto);
    }
}
