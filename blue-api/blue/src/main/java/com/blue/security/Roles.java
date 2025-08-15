package com.blue.security;

/**
 * Convenção centralizada de roles do sistema.
 * Spring Security espera prefixo "ROLE_" na GrantedAuthority,
 * mas aqui mantemos apenas a parte sem prefixo.
 */
public final class Roles {
    private Roles() {}

    public static final String ADMIN    = "ADMIN";
    public static final String OPERADOR = "OPERADOR";
    public static final String CLIENTE  = "CLIENTE";
}
