package com.blue.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cnpj;
}
