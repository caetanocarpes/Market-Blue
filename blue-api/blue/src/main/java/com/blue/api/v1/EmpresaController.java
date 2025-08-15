package com.blue.api.admin;

import com.blue.dto.EmpresaDTO;
import com.blue.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciar dados da empresa do admin logado.
 */
@RestController
@RequestMapping("/api/admin/empresa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    /** Retorna os dados da empresa do admin logado. */
    @GetMapping
    public ResponseEntity<EmpresaDTO> minhaEmpresa() {
        return ResponseEntity.ok(empresaService.buscarMinhaEmpresa());
    }

    /** Atualiza os dados da empresa do admin logado. */
    @PutMapping
    public ResponseEntity<EmpresaDTO> atualizarMinhaEmpresa(@RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.atualizarMinhaEmpresa(dto));
    }
}
