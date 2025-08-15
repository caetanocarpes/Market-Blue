package com.blue.repository;

import com.blue.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Listar todos os usuários do tenant
    List<Usuario> findByEmpresaId(Long empresaId);

    // Buscar um usuário específico garantindo o tenant
    Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);

    // Por papel (ADMIN, OPERADOR, CLIENTE) no tenant
    List<Usuario> findByEmpresaIdAndRole(Long empresaId, String role);

    // (Opcional) checar e-mail dentro do tenant — útil se no futuro o e-mail não for global único
    boolean existsByEmpresaIdAndEmailIgnoreCase(Long empresaId, String email);
}
