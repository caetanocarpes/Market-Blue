package com.blue.api.v1;

import com.blue.dto.CadastroEmpresaAdminDTO;
import com.blue.service.CadastroInicialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CadastroInicialController {

    private final CadastroInicialService cadastroInicialService;

    /**
     * Endpoint para cadastrar empresa + admin inicial.
     * Rota pública (sem autenticação).
     */
    @PostMapping("/cadastrar-empresa-admin")
    public ResponseEntity<String> cadastrar(@RequestBody CadastroEmpresaAdminDTO dto) {
        cadastroInicialService.cadastrarEmpresaComAdmin(dto);
        return ResponseEntity.ok("Empresa e admin cadastrados com sucesso!");
    }
}
