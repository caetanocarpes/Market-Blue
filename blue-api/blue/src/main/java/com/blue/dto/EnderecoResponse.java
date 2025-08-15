package com.blue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EnderecoResponse {
    private Long id;
    private Long empresaId;
    private Long clienteId; // null = endereço da EMPRESA
    private String cep, uf, cidade, bairro, rua, numero, complemento, referencia;
    private Boolean principal;
    private Double latitude, longitude;
}
