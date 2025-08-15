package com.blue.config;

import com.blue.domain.Empresa;
import com.blue.domain.Usuario;
import com.blue.repository.EmpresaRepository;
import com.blue.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Se já existir alguma empresa, usa a primeira.
        Empresa empresa = empresaRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> {
                    Empresa e = new Empresa();
                    e.setNome("Blue LTDA");
                    e.setCnpj("00.000.000/0001-00"); // ajuste se tiver @Pattern
                    e.setTelefone("11999999999");    // <=== preenchido (era o que faltava)
                    e.setEmail("contato@blue.com");  // preenche também se for @NotBlank
                    return empresaRepository.save(e);
                });

        // Garante o admin
        Optional<Usuario> adminOpt = usuarioRepository.findByEmail("admin@blue.com");
        if (adminOpt.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@blue.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEmpresa(empresa);
            usuarioRepository.save(admin);
            System.out.println("[SEED] Usuário admin criado: admin@blue.com / admin123");
        }
    }
}
