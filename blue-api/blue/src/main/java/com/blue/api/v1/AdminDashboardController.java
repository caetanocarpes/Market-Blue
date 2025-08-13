package com.blue.api.v1;

import com.blue.api.dto.DashboardMetricsDTO;
import com.blue.api.dto.VendaResumoDTO;
import com.blue.api.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints que o dashboard (front) consome.
 * - /api/admin/metrics
 * - /api/admin/ultimas-vendas
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // ajuste se precisar restringir
public class AdminDashboardController {

    private final AdminDashboardService service;

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        // Futuro: trocar pelo service que consulta o banco
        DashboardMetricsDTO dto = service.buscarMetricasDoDia();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ultimas-vendas")
    public ResponseEntity<List<VendaResumoDTO>> getUltimasVendas() {
        // Futuro: trocar pela consulta real (ORDER BY data DESC LIMIT 20)
        List<VendaResumoDTO> lista = service.listarUltimasVendas();
        return ResponseEntity.ok(lista);
    }
}
