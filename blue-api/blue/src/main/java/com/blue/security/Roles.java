package com.blue.security;

public final class Roles {
    private Roles() {}

    public static final String ADMIN   = "ADMIN";
    public static final String GERENTE = "GERENTE";
    public static final String CLIENTE = "CLIENTE";

    public static String toAuthority(String role) {
        if (role == null || role.isBlank()) return "ROLE_USER";
        return role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }
}
