package com.blue.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CadastroEmpresaAdminDTO {
    // Dados da empresa
    private String nomeEmpresa;
    private String emailEmpresa;
    private String telefoneEmpresa;
    private String cnpj;

    // Dados do usuário admin
    private String nomeAdmin;
    private String emailAdmin;
    private String senhaAdmin;
}
