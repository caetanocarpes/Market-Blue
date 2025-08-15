package com.blue.service;

import com.blue.domain.Categoria;
import com.blue.domain.Empresa;
import com.blue.dto.CategoriaDTO;
import com.blue.repository.CategoriaRepository;
import com.blue.repository.EmpresaRepository;
import com.blue.security.JwtTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final JwtTenant tenant;

    private Long requireEmpresaId() {
        Long empresaId = tenant.getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("empresaId ausente no token JWT");
        return empresaId;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAllByEmpresaId(requireEmpresaId());
    }

    @Transactional(readOnly = true)
    public Page<CategoriaDTO> listarPaginado(int page, int size, String nome) {
        Long empresaId = requireEmpresaId();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("nome").ascending());
        Page<Categoria> pg;
        if (nome == null || nome.isBlank()) {
            // como não temos findAll pageable sem empresa no repo, pega lista e pagina em memória
            List<Categoria> all = categoriaRepository.findAllByEmpresaId(empresaId);
            int from = pageable.getPageNumber() * pageable.getPageSize();
            int to = Math.min(from + pageable.getPageSize(), all.size());
            List<Categoria> slice = from > to ? List.of() : all.subList(from, to);
            List<CategoriaDTO> mapped = slice.stream()
                    .map(c -> new CategoriaDTO(c.getId(), c.getNome()))
                    .toList();
            return new PageImpl<>(mapped, pageable, all.size());
        } else {
            // se quiser, pode criar um método no repo: findByEmpresaIdAndNomeContainingIgnoreCase
            List<Categoria> filtradas = categoriaRepository.findAllByEmpresaId(empresaId).stream()
                    .filter(c -> c.getNome() != null && c.getNome().toLowerCase().contains(nome.toLowerCase()))
                    .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                    .toList();
            int from = pageable.getPageNumber() * pageable.getPageSize();
            int to = Math.min(from + pageable.getPageSize(), filtradas.size());
            List<Categoria> slice = from > to ? List.of() : filtradas.subList(from, to);
            List<CategoriaDTO> mapped = slice.stream()
                    .map(c -> new CategoriaDTO(c.getId(), c.getNome()))
                    .toList();
            return new PageImpl<>(mapped, pageable, filtradas.size());
        }
    }

    @Transactional
    public Categoria criar(CategoriaDTO dto) {
        Long empresaId = requireEmpresaId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada"));

        // unicidade por tenant (já tem métodos no repo)
        if (categoriaRepository.existsByEmpresaIdAndNomeIgnoreCase(empresaId, dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome neste mercado.");
        }

        Categoria c = Categoria.builder()
                .nome(dto.getNome())
                .empresa(empresa)
                .build();

        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaDTO dto) {
        Long empresaId = requireEmpresaId();

        Categoria c = categoriaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada neste mercado."));

        // se mudou o nome, checa duplicidade
        if (dto.getNome() != null && !dto.getNome().equalsIgnoreCase(c.getNome())
                && categoriaRepository.existsByEmpresaIdAndNomeIgnoreCase(empresaId, dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome neste mercado.");
        }

        c.setNome(dto.getNome());
        return categoriaRepository.save(c);
    }

    @Transactional
    public void deletar(Long id) {
        Long empresaId = requireEmpresaId();
        Categoria c = categoriaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada neste mercado."));
        categoriaRepository.delete(c);
    }
}
