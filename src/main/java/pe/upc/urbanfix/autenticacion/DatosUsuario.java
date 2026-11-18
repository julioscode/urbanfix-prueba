package pe.upc.urbanfix.autenticacion;
public record DatosUsuario(String nombre, String correo, long condominioId, String departamento, String torre,
                           String turno, String especialidad, boolean disponible) {}
