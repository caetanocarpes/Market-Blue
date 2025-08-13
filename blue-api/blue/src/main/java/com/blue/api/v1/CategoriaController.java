package com.blue.api.v1;

import com.blue.domain.Categoria;
import com.blue.dto.CategoriaDTO;
import com.blue.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints da API pública para Categorias.
 */
@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Categoria> listar() {
        return service.listar();
    }

    @PostMapping
    public Categoria criar(@RequestBody @Valid CategoriaDTO dto) {
        return service.criar(dto);
    }
}
