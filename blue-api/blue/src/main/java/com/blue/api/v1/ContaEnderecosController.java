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
 * CRUD de endereços do CLIENTE logado (role CLIENTE).
 */
@RestController
@RequestMapping("/api/conta/enderecos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContaEnderecosController {

    private final EnderecoService service;
    private final JwtTenant tenant;

    @GetMapping
    public List<EnderecoResponse> listar() {
        return service.listarCliente(tenant.getEmpresaId(), tenant.getClienteId());
    }

    @PostMapping
    public EnderecoResponse criar(@Valid @RequestBody EnderecoRequest req) {
        return service.criarParaCliente(tenant.getEmpresaId(), tenant.getClienteId(), req);
    }

    @PutMapping("/{id}")
    public EnderecoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EnderecoRequest req) {
        return service.atualizarCliente(tenant.getEmpresaId(), tenant.getClienteId(), id, req);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletarCliente(tenant.getEmpresaId(), tenant.getClienteId(), id);
    }
}
