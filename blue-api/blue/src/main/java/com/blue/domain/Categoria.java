package com.blue.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa uma categoria de produtos no sistema.
 * Ex.: Hortifruti, Bebidas, Padaria...
 */
@Entity
@Table(name = "categorias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;
}
