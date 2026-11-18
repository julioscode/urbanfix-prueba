package pe.upc.urbanfix;

import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.*;

public class AutorizadorTest {

    private final Autorizador autorizador = new Autorizador();
    private final UsuarioFactory fabrica = new UsuarioFactory();
    private final DatosUsuario datos = new DatosUsuario("Test", "t@cond.pe", 10, "1", "A", "dia", "Gasfitería", true);

    private static final Map<Accion, Set<Rol>> ESPERADO = Map.of(
            Accion.REGISTRAR_INCIDENCIA, EnumSet.of(Rol.RESIDENTE, Rol.CONSERJE),
            Accion.ADJUNTAR_EVIDENCIA, EnumSet.of(Rol.RESIDENTE, Rol.TECNICO),
            Accion.LISTAR_INCIDENCIAS, EnumSet.allOf(Rol.class),
            Accion.ASIGNAR_TECNICO, EnumSet.of(Rol.ADMINISTRADOR, Rol.CONSERJE),
            Accion.CAMBIAR_ESTADO, EnumSet.of(Rol.TECNICO),
            Accion.CALIFICAR_INCIDENCIA, EnumSet.of(Rol.RESIDENTE),
            Accion.REGISTRAR_OBSERVACION, EnumSet.of(Rol.CONSERJE),
            Accion.PUBLICAR_AVISO, EnumSet.of(Rol.ADMINISTRADOR),
            Accion.VER_REPORTES, EnumSet.of(Rol.ADMINISTRADOR));

    @Test public void PU14_laMatrizDePermisosCoincideConLaTablaDeServicios() {
        for (Rol rol : Rol.values()) {
            Usuario usuario = fabrica.crearUsuario(rol, datos);
            for (Accion accion : Accion.values())
                assertEquals(rol + " en " + accion, ESPERADO.get(accion).contains(rol), autorizador.permite(usuario, accion));
        }
    }

    @Test public void PU14_cadaUsuarioConoceSuRol() {
        for (Rol rol : Rol.values()) assertEquals(rol, fabrica.crearUsuario(rol, datos).getRol());
    }

    @Test public void PU14_exigirNoLanzaExcepcionSiElRolTienePermiso() {
        autorizador.exigir(fabrica.crearUsuario(Rol.ADMINISTRADOR, datos), Accion.PUBLICAR_AVISO);
    }

    @Test public void PU14_exigirLanzaAccesoDenegadoSiElRolNoTienePermiso() {
        try {
            autorizador.exigir(fabrica.crearUsuario(Rol.RESIDENTE, datos), Accion.PUBLICAR_AVISO);
            fail("Debía lanzar AccesoDenegadoException");
        } catch (AccesoDenegadoException e) {
            assertTrue(e.getMessage().contains("RESIDENTE"));
            assertTrue(e.getMessage().contains("PUBLICAR_AVISO"));
        }
    }
}
