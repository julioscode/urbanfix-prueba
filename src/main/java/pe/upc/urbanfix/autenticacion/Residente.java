package pe.upc.urbanfix.autenticacion;
public class Residente extends Usuario {
    public Residente(String departamento, String torre) {}
    @Override public Rol getRol() { return Rol.RESIDENTE; }
}
