package com.blue.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO que o front espera em /api/admin/metrics
 * Campos com nomes diretos pra não precisar mapear no JS.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardMetricsDTO {
    private Integer usuariosAtivos;
    private Integer pedidos24h;
    private BigDecimal faturamento;
    private BigDecimal ticketMedio;

    private Deltas deltas;              // variações em %
    private List<PontoSerie> serie;     // opcional: série p/ o “gráfico” fake

    public Integer getUsuariosAtivos() { return usuariosAtivos; }
    public void setUsuariosAtivos(Integer usuariosAtivos) { this.usuariosAtivos = usuariosAtivos; }

    public Integer getPedidos24h() { return pedidos24h; }
    public void setPedidos24h(Integer pedidos24h) { this.pedidos24h = pedidos24h; }

    public BigDecimal getFaturamento() { return faturamento; }
    public void setFaturamento(BigDecimal faturamento) { this.faturamento = faturamento; }

    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }

    public Deltas getDeltas() { return deltas; }
    public void setDeltas(Deltas deltas) { this.deltas = deltas; }

    public List<PontoSerie> getSerie() { return serie; }
    public void setSerie(List<PontoSerie> serie) { this.serie = serie; }

    /** Deltas percentuais exibidos em verdinho/vermelho no front */
    public static class Deltas {
        private Double usuariosAtivos;
        private Double pedidos24h;
        private Double faturamento;
        private Double ticketMedio;

        public Double getUsuariosAtivos() { return usuariosAtivos; }
        public void setUsuariosAtivos(Double usuariosAtivos) { this.usuariosAtivos = usuariosAtivos; }
        public Double getPedidos24h() { return pedidos24h; }
        public void setPedidos24h(Double pedidos24h) { this.pedidos24h = pedidos24h; }
        public Double getFaturamento() { return faturamento; }
        public void setFaturamento(Double faturamento) { this.faturamento = faturamento; }
        public Double getTicketMedio() { return ticketMedio; }
        public void setTicketMedio(Double ticketMedio) { this.ticketMedio = ticketMedio; }
    }

    /** Pontos da série temporal (dia e valor) pro gráfico */
    public static class PontoSerie {
        private LocalDate t; // data do ponto
        private BigDecimal v; // valor do ponto

        public PontoSerie() {}
        public PontoSerie(LocalDate t, BigDecimal v) { this.t = t; this.v = v; }

        public LocalDate getT() { return t; }
        public void setT(LocalDate t) { this.t = t; }
        public BigDecimal getV() { return v; }
        public void setV(BigDecimal v) { this.v = v; }
    }
}
