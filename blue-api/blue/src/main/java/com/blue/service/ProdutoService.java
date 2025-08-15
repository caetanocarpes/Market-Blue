package com.blue.service;

import com.blue.domain.Categoria;
import com.blue.domain.Empresa;
import com.blue.domain.Produto;
import com.blue.dto.ProdutoDTO;
import com.blue.repository.CategoriaRepository;
import com.blue.repository.EmpresaRepository;
import com.blue.repository.ProdutoRepository;
import com.blue.security.JwtTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Regras de Produto com escopo por empresa (tenant).
 */
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final JwtTenant tenant;

    /** Lista os produtos do tenant atual (sem paginação). */
    @Transactional(readOnly = true)
    public List<Produto> listar() {
        Long empresaId = requireEmpresaId();
        return produtoRepository.findAllByEmpresaId(empresaId);
    }

    /**
     * Lista paginada com ordenação sem exigir método novo no repository.
     * Ordenações suportadas: nome, preco, estoque, id (padrão: nome).
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDTO> listarPaginado(int page, int size, String sortBy) {
        Long empresaId = requireEmpresaId();
        // carrega tudo do tenant
        List<Produto> todos = produtoRepository.findAllByEmpresaId(empresaId);

        // ordena em memória
        Comparator<Produto> comparator = switch (sortBy == null ? "nome" : sortBy.toLowerCase()) {
            case "preco" -> Comparator.comparing(Produto::getPreco, ProdutoService::nullSafeBigDecimal);
            case "estoque" -> Comparator.comparing(p -> nvl(p.getEstoque()));
            case "id" -> Comparator.comparing(Produto::getId, ProdutoService::nullSafeLong);
            default -> Comparator.comparing(p -> nvl(p.getNome()));
        };
        todos.sort(comparator);

        // pagina em memória
        int from = Math.max(page, 0) * Math.max(size, 1);
        int to = Math.min(from + Math.max(size, 1), todos.size());
        List<Produto> slice = from > to ? List.of() : todos.subList(from, to);

        // mapeia para DTO
        List<ProdutoDTO> content = slice.stream()
                .map(p -> ProdutoDTO.builder()
                        .id(p.getId())
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .preco(p.getPreco())
                        .estoque(p.getEstoque())
                        .categoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null)
                        .build())
                .toList();

        return new PageImpl<>(content, org.springframework.data.domain.PageRequest.of(page, size), todos.size());
    }

    /** Cria um produto no tenant atual, vinculando à categoria do MESMO tenant. */
    @Transactional
    public Produto criar(ProdutoDTO dto) {
        Long empresaId = requireEmpresaId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada"));

        // Garante que a categoria pertence ao mesmo tenant
        Categoria categoria = categoriaRepository.findByIdAndEmpresaId(dto.getCategoriaId(), empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada neste mercado."));

        // (Opcional) Checar duplicidade de nome no tenant
        if (produtoRepository.existsByEmpresaIdAndNomeIgnoreCase(empresaId, dto.getNome())) {
            throw new IllegalArgumentException("Já existe um produto com esse nome neste mercado.");
        }

        Produto p = Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .estoque(dto.getEstoque())
                .categoria(categoria)
                .empresa(empresa)
                .build();

        return produtoRepository.save(p);
    }

    // ================= helpers =================

    private Long requireEmpresaId() {
        Long empresaId = tenant.getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("empresaId ausente no token JWT");
        return empresaId;
    }

    // utils para ordenação null-safe
    private static String nvl(String s) { return s == null ? "" : s; }
    private static Integer nvl(Integer i) { return i == null ? 0 : i; }
    private static int nullSafeBigDecimal(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }
    private static int nullSafeLong(Long a, Long b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }
}
