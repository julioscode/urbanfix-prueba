package pe.upc.urbanfix.autenticacion;
import static pe.upc.urbanfix.autenticacion.Rol.*;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
public class Autorizador {
    private static final Map<Accion, Set<Rol>> PERMITIDOS = Map.of(
            Accion.REGISTRAR_INCIDENCIA, EnumSet.of(RESIDENTE, CONSERJE),
            Accion.ADJUNTAR_EVIDENCIA, EnumSet.of(RESIDENTE, TECNICO),
            Accion.LISTAR_INCIDENCIAS, EnumSet.allOf(Rol.class),
            Accion.ASIGNAR_TECNICO, EnumSet.of(ADMINISTRADOR, CONSERJE),
            Accion.CAMBIAR_ESTADO, EnumSet.of(TECNICO),
            Accion.CALIFICAR_INCIDENCIA, EnumSet.of(RESIDENTE),
            Accion.REGISTRAR_OBSERVACION, EnumSet.of(CONSERJE),
            Accion.PUBLICAR_AVISO, EnumSet.of(ADMINISTRADOR),
            Accion.VER_REPORTES, EnumSet.of(ADMINISTRADOR));
    public boolean permite(Usuario usuario, Accion accion) { return PERMITIDOS.get(accion).contains(usuario.getRol()); }
    public void exigir(Usuario usuario, Accion accion) {
        if (!permite(usuario, accion))
            throw new AccesoDenegadoException("El rol " + usuario.getRol() + " no puede ejecutar " + accion);
    }
}
