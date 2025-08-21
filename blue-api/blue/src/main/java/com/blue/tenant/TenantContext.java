package com.blue.tenant;

public final class TenantContext {
    private TenantContext() {}

    private static final ThreadLocal<Long> EMPRESA_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID    = new ThreadLocal<>();
    private static final ThreadLocal<Long> CLIENTE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE     = new ThreadLocal<>();

    public static void set(Long empresaId, Long userId, Long clienteId, String role) {
        EMPRESA_ID.set(empresaId);
        USER_ID.set(userId);
        CLIENTE_ID.set(clienteId);
        ROLE.set(role);
    }

    public static Long   getEmpresaId() { return EMPRESA_ID.get(); }
    public static Long   getUserId()    { return USER_ID.get(); }
    public static Long   getClienteId() { return CLIENTE_ID.get(); }
    public static String getRole()      { return ROLE.get(); }

    public static void clear() {
        EMPRESA_ID.remove();
        USER_ID.remove();
        CLIENTE_ID.remove();
        ROLE.remove();
    }
}
