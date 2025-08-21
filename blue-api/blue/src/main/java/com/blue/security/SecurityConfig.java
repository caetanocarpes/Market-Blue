package com.blue.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Se você definir um bean CorsConfigurationSource (CorsConfig),
    // o Spring injeta aqui automaticamente.
    private final CorsConfigurationSource corsConfigurationSource = null;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT = stateless; sem CSRF
                .csrf(csrf -> csrf.disable())
                // CORS (usa o bean se existir)
                .cors(cors -> {
                    if (corsConfigurationSource != null) cors.configurationSource(corsConfigurationSource);
                })
                // Tratamento REST: 401/403 sem redirect
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) -> res.sendError(FORBIDDEN.value(), "Forbidden"))
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Pré-flight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Páginas públicas + estáticos
                        .requestMatchers(HttpMethod.GET,
                                "/", "/index.html",
                                "/login.html", "/cadastro.html",
                                "/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico"
                        ).permitAll()

                        // APIs públicas (login/refresh/etc)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Cadastro público (remova se quiser fechar depois)
                        .requestMatchers(HttpMethod.POST, "/api/empresas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // Demais rotas protegidas
                        .anyRequest().authenticated()
                )
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
        return new ProviderManager(List.of(provider));
    }
}
