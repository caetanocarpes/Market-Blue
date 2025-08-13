package com.blue.api.service;

import com.blue.api.dto.DashboardMetricsDTO;
import com.blue.api.dto.VendaResumoDTO;

import java.util.List;

/**
 * Camada de serviço pra isolar a regra de negócio.
 * Depois pode trocar a implementação mock por uma que consulta o banco.
 */
public interface AdminDashboardService {
    DashboardMetricsDTO buscarMetricasDoDia();
    List<VendaResumoDTO> listarUltimasVendas();
}
