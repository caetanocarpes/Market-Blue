package com.blue.api.v1;

import com.blue.dto.DashboardMetricsDTO;
import com.blue.dto.VendaResumoDTO;
import com.blue.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints do dashboard administrativo.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/metrics")
    public DashboardMetricsDTO metrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate
    ) {
        return dashboardService.obterKpis(de, ate);
    }

    @GetMapping("/ultimas-vendas")
    public List<VendaResumoDTO> ultimasVendas(@RequestParam(defaultValue = "10") int limit) {
        return dashboardService.ultimasVendas(limit);
    }
}
