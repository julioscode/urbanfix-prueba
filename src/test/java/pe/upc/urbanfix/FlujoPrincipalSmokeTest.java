package pe.upc.urbanfix;

import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.*;
import pe.upc.urbanfix.incidencias.domain.*;
import pe.upc.urbanfix.notificaciones.*;
import pe.upc.urbanfix.nucleo.RegistroAuditoria;

public class FlujoPrincipalSmokeTest {

    @Test
    public void humo_registrarAsignarAtenderResolverYCerrarUnTicket() {
        UsuarioFactory fabrica = new UsuarioFactory();
        DatosUsuario datos = new DatosUsuario("Luis", "luis@cond.pe", 10, "", "", "", "Gasfitería", true);
        Usuario residente = fabrica.crearUsuario(Rol.RESIDENTE, datos);
        assertEquals(10L, residente.getCondominioId());

        List<String> correos = new ArrayList<>();
        SmtpClient smtp = (destino, asunto, mensaje) -> correos.add(mensaje);
        RegistroAuditoria auditoria = new RegistroAuditoria();
        NotificationService notificaciones =
                new NotificationService(List.of(new EmailAdapter(smtp)), residente.getCorreo());
        IncidenciaService servicio = new IncidenciaService(List.of(auditoria, notificaciones));
        IncidenciaRepository repositorio = new IncidenciaRepository();

        Incidencia ticket = inc(1, 10, "Gasfitería");
        repositorio.save(ticket);
        assertEquals(Estado.REPORTADO, ticket.getEstadoActual().codigo());

        List<Tecnico> tecnicos = List.of(tec(1, "Gasfitería", 2), tec(2, "Gasfitería", 1), tec(3, "Electricidad", 0));
        Tecnico elegido = new AsignacionMenorCargaStrategy().elegirTecnico(ticket, tecnicos).orElseThrow();
        assertEquals(2L, elegido.getId());

        servicio.asignar(ticket, elegido, "admin@cond.pe");
        servicio.iniciar(ticket, elegido, "tecnico@cond.pe");
        ticket.agregarEvidencia("foto-reparacion.jpg");
        servicio.resolver(ticket, "Se reparó la fuga", "tecnico@cond.pe");
        servicio.cerrar(ticket, 5, "luis@cond.pe");

        assertEquals(Estado.CERRADO, ticket.getEstadoActual().codigo());
        assertEquals(4, auditoria.getRegistros().size());
        assertEquals(4, correos.size());
        assertTrue(repositorio.buscarPorIdYCondominio(1, 10).isPresent());
        assertTrue(repositorio.buscarPorIdYCondominio(1, 20).isEmpty());
    }
}
