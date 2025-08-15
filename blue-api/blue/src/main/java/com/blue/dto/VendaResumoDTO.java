package com.blue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Linha da tabela "Últimas vendas" no dashboard. */
public class VendaResumoDTO {
    private Long id;
    private String cliente;
    private String produto;
    private BigDecimal valor;
    private LocalDateTime data;

    public VendaResumoDTO() {}

    public VendaResumoDTO(Long id, String cliente, String produto, BigDecimal valor, LocalDateTime data) {
        this.id = id;
        this.cliente = cliente;
        this.produto = produto;
        this.valor = valor;
        this.data = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
