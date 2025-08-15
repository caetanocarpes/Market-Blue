package com.blue.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Payload de criação/edição de endereço.
 * empresaId/clienteId vêm do token (JwtTenant).
 */
@Getter @Setter
public class EnderecoRequest {

    @NotBlank
    @Pattern(regexp = "\\d{5}-\\d{3}")
    private String cep;

    @NotBlank @Size(min = 2, max = 2)
    private String uf;

    @NotBlank private String cidade;
    @NotBlank private String bairro;
    @NotBlank private String rua;
    @NotBlank private String numero;

    private String complemento;
    private String referencia;

    private Boolean principal = false;
}
