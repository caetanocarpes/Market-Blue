package com.blue.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Objeto usado para receber/enviar dados de Categoria.
 */
public record CategoriaDTO(
        Long id,
        @NotBlank(message = "Nome é obrigatório") String nome,
        String descricao
) {}
