package com.blue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;
    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethods;
    @Value("${cors.allowed-headers:Authorization,Content-Type,Accept}")
    private String allowedHeaders;
    @Value("${cors.allow-credentials:false}")
    private boolean allowCredentials;
    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        cfg.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        cfg.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        cfg.setAllowCredentials(allowCredentials);
        cfg.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
