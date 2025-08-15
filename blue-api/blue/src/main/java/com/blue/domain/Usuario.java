package com.blue.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "usuarios",
        indexes = {
                @Index(name = "idx_usuario_empresa", columnList = "empresa_id"),
                @Index(name = "idx_usuario_email", columnList = "email", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha; // armazenar com hash (BCrypt)

    @NotBlank
    @Column(nullable = false)
    private String role; // ADMIN, OPERADOR, CLIENTE

    /** Escopo multi-tenant: o usuário pertence a uma EMPRESA. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    /**
     * Opcional: vínculo com "cliente" (quando o usuário for role CLIENTE).
     * Facilita gerar o claim clienteId no JWT.
     */
    @Column(name = "cliente_id")
    private Long clienteId;
}
