package pe.upc.urbanfix.autenticacion;
public class Administrador extends Usuario {
    public Administrador() {}
    @Override public Rol getRol() { return Rol.ADMINISTRADOR; }
}
