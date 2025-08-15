package com.blue.api.v1;

import com.blue.domain.Produto;
import com.blue.dto.ProdutoDTO;
import com.blue.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints da API para Produtos.
 * GET simples: lista do tenant.
 * GET paginado: pagina/ordena em memória (conforme service).
 * POST: cria produto no tenant atual.
 */
@RestController
@RequestMapping("/api/v1/produtos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    /** Lista sem paginação (apenas do tenant atual). */
    @GetMapping
    public List<Produto> listar() {
        return service.listar();
    }

    /** Lista paginada e ordenada em memória. sortBy: nome|preco|estoque|id */
    @GetMapping("/page")
    public Page<ProdutoDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy
    ) {
        return service.listarPaginado(page, size, sortBy);
    }

    /** Cria um produto no tenant atual. */
    @PostMapping
    public Produto criar(@RequestBody @Valid ProdutoDTO dto) {
        return service.criar(dto);
    }
}
