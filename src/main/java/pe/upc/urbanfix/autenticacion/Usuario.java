package pe.upc.urbanfix.autenticacion;
public abstract class Usuario {
    private String nombre, correo; private long condominioId;
    public void inicializar(String n, String c, long cond) { nombre = n; correo = c; condominioId = cond; }
    public String getNombre() { return nombre; } public String getCorreo() { return correo; } public long getCondominioId() { return condominioId; }
    public abstract Rol getRol();
}
