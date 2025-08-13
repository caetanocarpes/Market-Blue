package com.blue.security;

import com.blue.domain.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Classe utilitária para recuperar o usuário logado no sistema.
 */
@Component
public class UsuarioAutenticadoUtil {

    /**
     * Retorna o objeto Usuario do usuário autenticado.
     */
    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }

        return null; // Se não houver usuário logado
    }

    /**
     * Retorna o ID da empresa do usuário autenticado.
     */
    public Long getEmpresaIdUsuario() {
        Usuario usuario = getUsuarioAutenticado();
        return (usuario != null && usuario.getEmpresa() != null) ? usuario.getEmpresa().getId() : null;
    }
}
