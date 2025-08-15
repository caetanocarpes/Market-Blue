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
 * Gestão de endereços de um CLIENTE específico pela visão ADMIN/OPERADOR.
 * Escopo sempre do empresaId do token.
 */
@RestController
@RequestMapping("/api/admin/enderecos/clientes/{clienteId}")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEnderecoClienteController {

    private final EnderecoService service;
    private final JwtTenant tenant;

    @GetMapping
    public List<EnderecoResponse> listar(@PathVariable Long clienteId) {
        return service.listarCliente(tenant.getEmpresaId(), clienteId);
    }

    @PostMapping
    public EnderecoResponse criar(@PathVariable Long clienteId, @Valid @RequestBody EnderecoRequest req) {
        return service.criarParaCliente(tenant.getEmpresaId(), clienteId, req);
    }

    @PutMapping("/{enderecoId}")
    public EnderecoResponse atualizar(@PathVariable Long clienteId,
                                      @PathVariable Long enderecoId,
                                      @Valid @RequestBody EnderecoRequest req) {
        return service.atualizarCliente(tenant.getEmpresaId(), clienteId, enderecoId, req);
    }

    @DeleteMapping("/{enderecoId}")
    public void deletar(@PathVariable Long clienteId, @PathVariable Long enderecoId) {
        service.deletarCliente(tenant.getEmpresaId(), clienteId, enderecoId);
    }
}
