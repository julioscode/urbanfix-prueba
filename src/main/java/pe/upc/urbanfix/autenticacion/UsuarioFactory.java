package pe.upc.urbanfix.autenticacion;
public class UsuarioFactory {
    public Usuario crearUsuario(Rol rol, DatosUsuario d) {
        Usuario u = switch (rol) {
            case RESIDENTE -> new Residente(d.departamento(), d.torre());
            case CONSERJE -> new Conserje(d.turno());
            case TECNICO -> new Tecnico(d.especialidad(), d.disponible());
            case ADMINISTRADOR -> new Administrador();
        };
        u.inicializar(d.nombre(), d.correo(), d.condominioId());
        return u;
    }
}
