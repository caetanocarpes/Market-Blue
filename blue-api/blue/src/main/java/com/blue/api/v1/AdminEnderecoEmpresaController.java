package com.blue.api.v1;

import com.blue.dto.EnderecoRequest;
import com.blue.dto.EnderecoResponse;
import com.blue.security.JwtTenant;
import com.blue.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endereços institucionais da EMPRESA (clienteId = null).
 * Escopo: empresaId do token (ADMIN/OPERADOR).
 */
@RestController
@RequestMapping("/api/admin/enderecos/empresa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEnderecoEmpresaController {

    private final EnderecoService service;
    private final JwtTenant tenant;

    @GetMapping
    public List<EnderecoResponse> listar() {
        return service.listarEmpresa(tenant.getEmpresaId());
    }

    @PostMapping
    public EnderecoResponse criar(@Valid @RequestBody EnderecoRequest req) {
        return service.criarParaEmpresa(tenant.getEmpresaId(), req);
    }

    @PutMapping("/{id}")
    public EnderecoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EnderecoRequest req) {
        return service.atualizarEmpresa(tenant.getEmpresaId(), id, req);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletarEmpresa(tenant.getEmpresaId(), id);
    }
}
