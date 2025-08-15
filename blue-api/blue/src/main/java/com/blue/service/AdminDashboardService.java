package com.blue.service;

import com.blue.dto.DashboardMetricsDTO;
import com.blue.dto.VendaResumoDTO;
import com.blue.repository.UsuarioRepository;
import com.blue.security.JwtTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service do dashboard administrativo.
 * Versão inicial sem pedidos: preenche métricas básicas com dados disponíveis
 * e stubs para os campos ainda não implementados.
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTenant tenant;

    /** KPIs do período (por enquanto, alguns valores são stubs até termos Pedidos). */
    @Transactional(readOnly = true)
    public DashboardMetricsDTO obterKpis(LocalDate de, LocalDate ate) {
        Long empresaId = tenant.getEmpresaId();

        int usuariosAtivos = usuarioRepository.findByEmpresaId(empresaId).size();

        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setUsuariosAtivos(usuariosAtivos);
        dto.setPedidos24h(0); // ainda não temos pedidos implementados
        dto.setFaturamento(BigDecimal.ZERO);
        dto.setTicketMedio(BigDecimal.ZERO);

        DashboardMetricsDTO.Deltas deltas = new DashboardMetricsDTO.Deltas();
        deltas.setUsuariosAtivos(0.0);
        deltas.setPedidos24h(0.0);
        deltas.setFaturamento(0.0);
        deltas.setTicketMedio(0.0);
        dto.setDeltas(deltas);

        // Série temporal dummy (últimos 7 dias)
        List<DashboardMetricsDTO.PontoSerie> serie = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            serie.add(new DashboardMetricsDTO.PontoSerie(LocalDate.now().minusDays(i), BigDecimal.ZERO));
        }
        dto.setSerie(serie);

        return dto;
    }

    /** Últimas vendas (stub sem pedidos): retorna lista vazia por enquanto. */
    @Transactional(readOnly = true)
    public List<VendaResumoDTO> ultimasVendas(int limit) {
        return List.of(); // quando houver Pedido, preenche aqui
    }
}
