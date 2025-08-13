package com.blue.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private Long empresaId;
}
