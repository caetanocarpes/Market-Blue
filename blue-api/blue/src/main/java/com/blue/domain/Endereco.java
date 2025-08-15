package com.blue.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Endereço genérico para EMPRESA (clienteId = null) e para CLIENTE (clienteId != null).
 * Campo empresaId garante o escopo multi-tenant.
 */
@Entity
@Table(name = "enderecos",
        indexes = {
                @Index(name = "idx_end_emp", columnList = "empresa_id"),
                @Index(name = "idx_end_emp_cli", columnList = "empresa_id,cliente_id"),
                @Index(name = "idx_end_cep", columnList = "cep")
        })
@Getter @Setter @NoArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Multi-tenant: todo endereço pertence a uma empresa (mercado)
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    // Se for null => endereço da EMPRESA; se não for null => endereço do CLIENTE
    @Column(name = "cliente_id")
    private Long clienteId;

    @NotBlank
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
    private String cep;

    @NotBlank
    @Size(min = 2, max = 2)
    private String uf;

    @NotBlank @Size(max = 120)
    private String cidade;

    @NotBlank @Size(max = 120)
    private String bairro;

    @NotBlank @Size(max = 180)
    private String rua;

    @NotBlank @Size(max = 20)
    private String numero;

    @Size(max = 120)
    private String complemento;

    @Size(max = 180)
    private String referencia;

    // Apenas um principal por empresa (para endereço institucional) e por cliente (endereço padrão)
    @Column(nullable = false)
    private Boolean principal = false;

    // Campos para futuro cálculo de frete (opcionais)
    private Double latitude;
    private Double longitude;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @PreUpdate
    public void atualizaTimestamp() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
