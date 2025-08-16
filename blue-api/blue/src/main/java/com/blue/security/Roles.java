package com.blue.security;

public final class Roles {
    private Roles() {}

    public static final String ADMIN   = "ADMIN";
    public static final String GERENTE = "GERENTE";
    public static final String CLIENTE = "CLIENTE";

    /** Converte "ADMIN" -> "ROLE_ADMIN" para o Spring Security */
    public static String toAuthority(String role) {
        if (role == null || role.isBlank()) return "ROLE_USER";
        return role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
    }
}
