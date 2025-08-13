package com.blue.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Objeto usado para receber/enviar dados de Produto.
 */
public record ProdutoDTO(
        Long id,
        @NotBlank(message = "Nome é obrigatório") String nome,
        String descricao,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal preco,
        @NotNull @Min(0) Integer estoque,
        @NotNull Long categoriaId
) {}
