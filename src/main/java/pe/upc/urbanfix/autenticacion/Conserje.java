package pe.upc.urbanfix.autenticacion;
public class Conserje extends Usuario {
    public Conserje(String turno) {}
    @Override public Rol getRol() { return Rol.CONSERJE; }
}
