package com.blue.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Categoria de produtos (escopada por EMPRESA).
 * Ex.: Hortifruti, Bebidas, Padaria...
 */
@Entity
@Table(
        name = "categorias",
        uniqueConstraints = {
                // Garante unicidade do nome DENTRO da mesma empresa
                @UniqueConstraint(name = "uk_categoria_empresa_nome", columnNames = {"empresa_id", "nome"})
        },
        indexes = {
                @Index(name = "idx_categoria_empresa", columnList = "empresa_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    /** Escopo multi-tenant: toda categoria pertence a uma EMPRESA. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
