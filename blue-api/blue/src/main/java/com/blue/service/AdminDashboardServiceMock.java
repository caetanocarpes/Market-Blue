package com.blue.service;

import com.blue.dto.DashboardMetricsDTO;
import com.blue.dto.VendaResumoDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementação MOCK: gera dados aleatórios coerentes com supermercados.
 * Ative com o profile "mock-dashboard" e ajuste o controller para usar esta classe se quiser.
 */
@Service
@Profile("mock-dashboard")
public class AdminDashboardServiceMock {

    private static final List<String> NOMES = List.of(
            "Ana", "Bruno", "Carla", "Diego", "Edu", "Fernanda", "Gabi", "Hugo", "Igor", "Julia",
            "Kaua", "Larissa", "Marcos", "Nina", "Otávio", "Paula", "Rafa", "Sofia", "Tiago", "Vivi"
    );

    private static final List<String> PRODUTOS_MERCADO = List.of(
            "Arroz 5kg", "Feijão Preto 1kg", "Macarrão Espaguete 500g", "Açúcar 1kg",
            "Óleo de Soja 900ml", "Leite Integral 1L", "Pão Francês 1kg", "Manteiga 200g",
            "Café Torrado 500g", "Refrigerante 2L", "Suco de Laranja 1L", "Água Mineral 1,5L",
            "Frango Inteiro 1kg", "Carne Bovina Patinho 1kg", "Linguiça Toscana 1kg",
            "Tomate 1kg", "Banana Nanica 1kg", "Maçã Gala 1kg", "Alface Crespa (unid.)",
            "Detergente 500ml", "Sabão em Pó 1kg", "Papel Higiênico 12 rolos",
            "Shampoo 350ml", "Sabonete 90g", "Queijo Mussarela 200g", "Presunto 200g",
            "Farinha de Trigo 1kg", "Molho de Tomate 340g", "Biscoito Recheado 130g",
            "Cerveja Lata 350ml (unid.)"
    );

    private final Random r = new Random();

    /** Mesmas assinaturas do service "real" pra facilitar troca. */
    public DashboardMetricsDTO obterKpis(LocalDate de, LocalDate ate) {
        int usuariosAtivos = rnd(120, 420);
        int pedidos24h = rnd(40, 180);
        BigDecimal faturamento = money(r.nextDouble() * 45000 + 5000);
        BigDecimal ticketMedio = pedidos24h == 0 ? BigDecimal.ZERO :
                money(faturamento.doubleValue() / pedidos24h);

        DashboardMetricsDTO.Deltas deltas = new DashboardMetricsDTO.Deltas();
        deltas.setUsuariosAtivos(rndPct(-2.0, 9.0));
        deltas.setPedidos24h(rndPct(-4.0, 5.0));
        deltas.setFaturamento(rndPct(0.0, 12.0));
        deltas.setTicketMedio(rndPct(0.0, 8.0));

        // Série de 14 dias para o gráfico
        List<DashboardMetricsDTO.PontoSerie> serie = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        for (int i = 13; i >= 0; i--) {
            LocalDate dia = hoje.minusDays(i);
            BigDecimal valorDia = money(15000 + r.nextDouble() * 20000);
            serie.add(new DashboardMetricsDTO.PontoSerie(dia, valorDia));
        }

        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setUsuariosAtivos(usuariosAtivos);
        dto.setPedidos24h(pedidos24h);
        dto.setFaturamento(faturamento);
        dto.setTicketMedio(ticketMedio);
        dto.setDeltas(deltas);
        dto.setSerie(serie);
        return dto;
    }

    public List<VendaResumoDTO> ultimasVendas(int limit) {
        int quantidade = Math.min(limit, rnd(6, 12));
        List<VendaResumoDTO> lista = new ArrayList<>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            String cliente = pick(NOMES);
            String produto = pick(PRODUTOS_MERCADO);
            BigDecimal valor = money(2 + r.nextDouble() * 80); // R$2 ~ R$82
            LocalDateTime data = LocalDateTime.now().minusMinutes(rnd(5, 60 * 60));

            lista.add(new VendaResumoDTO((long) (1000 + i), cliente, produto, valor, data));
        }
        // Ordena por data desc (mais recente primeiro)
        lista.sort(Comparator.comparing(VendaResumoDTO::getData).reversed());
        return lista;
    }

    /* ===== Helpers ===== */
    private int rnd(int min, int max) { return r.nextInt((max - min) + 1) + min; }
    private double rndPct(double min, double max) { return round1(min + r.nextDouble() * (max - min)); }
    private double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private <T> T pick(List<T> list) { return list.get(r.nextInt(list.size())); }
    private BigDecimal money(double v) { return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP); }
}
