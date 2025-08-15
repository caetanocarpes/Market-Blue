package com.blue.security;

import com.blue.domain.Usuario;
import com.blue.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Se o repo tiver ignore-case, prefira: findByEmailIgnoreCase(username)
        Usuario u = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        String role = (u.getRole() == null ? "USER" : u.getRole()).toUpperCase();
        List<SimpleGrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return User.withUsername(u.getEmail())
                .password(u.getSenha()) // BCrypt armazenado no banco
                .authorities(auths)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false) // ajuste para !u.isAtivo() se tiver flag
                .build();
    }
}
