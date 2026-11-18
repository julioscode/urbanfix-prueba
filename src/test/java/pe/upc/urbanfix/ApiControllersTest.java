package pe.upc.urbanfix;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import pe.upc.urbanfix.api.*;
import pe.upc.urbanfix.autenticacion.*;
import pe.upc.urbanfix.incidencias.domain.*;

public class ApiControllersTest {

    private static final String CLAVE = "clave-segura-1";

    private AutenticacionService autenticacion;
    private IncidenciaRepository repositorio;
    private IncidenciaService servicio;
    private IncidenciaController incidencias;
    private AuthController auth;
    private Tecnico tecnico;
    private String tokenResidente, tokenConserje, tokenTecnico, tokenAdmin, tokenOtroCondominio;
    private final Categoria fuga = new Categoria("Fuga de agua", "Gasfitería");

    private String registrar(Usuario u) {
        autenticacion.registrar(u, CLAVE);
        return autenticacion.login(u.getCorreo(), CLAVE);
    }

    private Usuario usuario(Rol rol, String correo, long condominio) {
        Usuario u = new UsuarioFactory().crearUsuario(rol, new DatosUsuario("N", correo, condominio, "1", "A", "dia", "Gasfitería", true));
        return u;
    }

    @Before public void preparar() {
        autenticacion = new AutenticacionService();
        repositorio = new IncidenciaRepository();
        servicio = new IncidenciaService(List.of(), repositorio);
        incidencias = new IncidenciaController(autenticacion, new Autorizador(), servicio, repositorio);
        auth = new AuthController(autenticacion);
        tecnico = new Tecnico(5, "Gasfitería", true, 0);
        tecnico.inicializar("Tec", "tec@cond.pe", 10);
        tokenResidente = registrar(usuario(Rol.RESIDENTE, "luis@cond.pe", 10));
        tokenConserje = registrar(usuario(Rol.CONSERJE, "conserje@cond.pe", 10));
        tokenTecnico = registrar(tecnico);
        tokenAdmin = registrar(usuario(Rol.ADMINISTRADOR, "admin@cond.pe", 10));
        tokenOtroCondominio = registrar(usuario(Rol.RESIDENTE, "otro@otro.pe", 20));
    }

    @Test public void PU16_loginCorrectoResponde200ConToken() {
        Respuesta r = auth.login("luis@cond.pe", CLAVE);
        assertEquals(200, r.estado());
        assertTrue(((Map<?, ?>) r.cuerpo()).containsKey("token"));
    }

    @Test public void PU16_loginIncorrectoResponde401() {
        assertEquals(401, auth.login("luis@cond.pe", "mala-clave-1").estado());
    }

    @Test public void PU16_sinSesionValidaResponde401() {
        assertEquals(401, incidencias.listar(null).estado());
        assertEquals(401, incidencias.registrar("falso", fuga, "A", false).estado());
    }

    @Test public void PU16_residenteYConserjeRegistranIncidenciasConRespuesta201() {
        assertEquals(201, incidencias.registrar(tokenResidente, fuga, "Torre A", false).estado());
        assertEquals(201, incidencias.registrar(tokenConserje, fuga, "Torre B", false).estado());
    }

    @Test public void PU16_unTecnicoNoPuedeRegistrarIncidenciasYResponde403() {
        assertEquals(403, incidencias.registrar(tokenTecnico, fuga, "Torre A", false).estado());
    }

    @Test public void PU16_unDuplicadoResponde409YConfirmadoResponde201() {
        incidencias.registrar(tokenResidente, fuga, "Torre A", false);
        assertEquals(409, incidencias.registrar(tokenResidente, fuga, "Torre A", false).estado());
        assertEquals(201, incidencias.registrar(tokenResidente, fuga, "Torre A", true).estado());
    }

    @Test public void PU16_listarSoloDevuelveLosTicketsDelCondominioDelUsuario() {
        incidencias.registrar(tokenResidente, fuga, "Torre A", false);
        incidencias.registrar(tokenOtroCondominio, fuga, "Torre Z", false);
        Respuesta r = incidencias.listar(tokenAdmin);
        assertEquals(200, r.estado());
        assertEquals(1, ((List<?>) r.cuerpo()).size());
    }

    @Test public void PU16_unTicketDeOtroCondominioResponde404() {
        incidencias.registrar(tokenResidente, fuga, "Torre A", false);
        assertEquals(404, incidencias.calificar(tokenOtroCondominio, 1, 5).estado());
    }

    @Test public void PU16_soloElResidenteCalificaYElResultadoDependeDelEstado() {
        incidencias.registrar(tokenResidente, fuga, "Torre A", false);
        assertEquals(403, incidencias.calificar(tokenConserje, 1, 5).estado());
        assertEquals(409, incidencias.calificar(tokenResidente, 1, 5).estado());
    }

    @Test public void PU16_flujoCompletoDelTecnicoConLosCodigosDeRespuesta() {
        Incidencia ticket = (Incidencia) incidencias.registrar(tokenResidente, fuga, "Torre A", false).cuerpo();
        assertEquals(403, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "INICIAR", null).estado());
        servicio.asignar(ticket, tecnico, "admin@cond.pe");
        assertEquals(200, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "INICIAR", null).estado());
        assertEquals(409, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "RESOLVER", "listo").estado());
        ticket.agregarEvidencia("foto.jpg");
        assertEquals(200, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "RESOLVER", "listo").estado());
        assertEquals(200, incidencias.calificar(tokenResidente, ticket.getId(), 5).estado());
        assertEquals(Estado.CERRADO, ticket.getEstadoActual().codigo());
    }

    @Test public void PU16_unTecnicoNoAsignadoNoPuedeCambiarElEstado() {
        Incidencia ticket = (Incidencia) incidencias.registrar(tokenResidente, fuga, "Torre A", false).cuerpo();
        Tecnico otro = new Tecnico(9, "Gasfitería", true, 0);
        servicio.asignar(ticket, otro, "admin@cond.pe");
        assertEquals(403, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "INICIAR", null).estado());
    }

    @Test public void PU16_unaAccionDesconocidaResponde400YUnTicketInexistenteResponde404() {
        Incidencia ticket = (Incidencia) incidencias.registrar(tokenResidente, fuga, "Torre A", false).cuerpo();
        servicio.asignar(ticket, tecnico, "admin@cond.pe");
        assertEquals(400, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), "BORRAR", null).estado());
        assertEquals(400, incidencias.cambiarEstado(tokenTecnico, ticket.getId(), null, null).estado());
        assertEquals(404, incidencias.cambiarEstado(tokenTecnico, 999, "INICIAR", null).estado());
    }
}
