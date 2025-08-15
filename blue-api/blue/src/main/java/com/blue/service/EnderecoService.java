package com.blue.service;

import com.blue.domain.Endereco;
import com.blue.dto.EnderecoRequest;
import com.blue.dto.EnderecoResponse;
import com.blue.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio dos endereços:
 * - Escopo por empresaId obrigatório.
 * - Se marcar principal=true, desmarca os demais do mesmo escopo.
 */
@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository repo;

    // ===== Listagens =====

    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarEmpresa(Long empresaId) {
        return repo.findByEmpresaIdAndClienteIdIsNull(empresaId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarCliente(Long empresaId, Long clienteId) {
        return repo.findByEmpresaIdAndClienteId(empresaId, clienteId)
                .stream().map(this::toDto).toList();
    }

    // ===== Criação =====

    @Transactional
    public EnderecoResponse criarParaEmpresa(Long empresaId, EnderecoRequest req) {
        Endereco e = fromReq(req);
        e.setEmpresaId(empresaId);
        e.setClienteId(null);
        Endereco salvo = repo.save(e);
        if (Boolean.TRUE.equals(salvo.getPrincipal())) {
            desmarcarOutrosPrincipaisEmpresa(empresaId, salvo.getId());
        }
        return toDto(salvo);
    }

    @Transactional
    public EnderecoResponse criarParaCliente(Long empresaId, Long clienteId, EnderecoRequest req) {
        Endereco e = fromReq(req);
        e.setEmpresaId(empresaId);
        e.setClienteId(clienteId);
        Endereco salvo = repo.save(e);
        if (Boolean.TRUE.equals(salvo.getPrincipal())) {
            desmarcarOutrosPrincipaisCliente(empresaId, clienteId, salvo.getId());
        }
        return toDto(salvo);
    }

    // ===== Atualização =====

    @Transactional
    public EnderecoResponse atualizarEmpresa(Long empresaId, Long enderecoId, EnderecoRequest req) {
        Endereco e = repo.findByIdAndEmpresaId(enderecoId, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        aplicar(e, req);
        Endereco salvo = repo.save(e);
        if (Boolean.TRUE.equals(salvo.getPrincipal())) {
            desmarcarOutrosPrincipaisEmpresa(empresaId, salvo.getId());
        }
        return toDto(salvo);
    }

    @Transactional
    public EnderecoResponse atualizarCliente(Long empresaId, Long clienteId, Long enderecoId, EnderecoRequest req) {
        Endereco e = repo.findByIdAndEmpresaIdAndClienteId(enderecoId, empresaId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        aplicar(e, req);
        Endereco salvo = repo.save(e);
        if (Boolean.TRUE.equals(salvo.getPrincipal())) {
            desmarcarOutrosPrincipaisCliente(empresaId, clienteId, salvo.getId());
        }
        return toDto(salvo);
    }

    // ===== Exclusão =====

    @Transactional
    public void deletarEmpresa(Long empresaId, Long enderecoId) {
        Endereco e = repo.findByIdAndEmpresaId(enderecoId, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        repo.delete(e);
    }

    @Transactional
    public void deletarCliente(Long empresaId, Long clienteId, Long enderecoId) {
        Endereco e = repo.findByIdAndEmpresaIdAndClienteId(enderecoId, empresaId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        repo.delete(e);
    }

    // ===== Helpers =====

    private void desmarcarOutrosPrincipaisEmpresa(Long empresaId, Long manterId) {
        repo.findByEmpresaIdAndClienteIdIsNull(empresaId).forEach(end -> {
            if (!end.getId().equals(manterId) && Boolean.TRUE.equals(end.getPrincipal())) {
                end.setPrincipal(false);
                repo.save(end);
            }
        });
    }

    private void desmarcarOutrosPrincipaisCliente(Long empresaId, Long clienteId, Long manterId) {
        repo.findByEmpresaIdAndClienteId(empresaId, clienteId).forEach(end -> {
            if (!end.getId().equals(manterId) && Boolean.TRUE.equals(end.getPrincipal())) {
                end.setPrincipal(false);
                repo.save(end);
            }
        });
    }

    private Endereco fromReq(EnderecoRequest r) {
        Endereco e = new Endereco();
        aplicar(e, r);
        return e;
    }

    private void aplicar(Endereco e, EnderecoRequest r) {
        e.setCep(r.getCep());
        e.setUf(r.getUf());
        e.setCidade(r.getCidade());
        e.setBairro(r.getBairro());
        e.setRua(r.getRua());
        e.setNumero(r.getNumero());
        e.setComplemento(r.getComplemento());
        e.setReferencia(r.getReferencia());
        e.setPrincipal(Boolean.TRUE.equals(r.getPrincipal()));
    }

    private EnderecoResponse toDto(Endereco e) {
        return EnderecoResponse.builder()
                .id(e.getId())
                .empresaId(e.getEmpresaId())
                .clienteId(e.getClienteId())
                .cep(e.getCep())
                .uf(e.getUf())
                .cidade(e.getCidade())
                .bairro(e.getBairro())
                .rua(e.getRua())
                .numero(e.getNumero())
                .complemento(e.getComplemento())
                .referencia(e.getReferencia())
                .principal(e.getPrincipal())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .build();
    }
}
