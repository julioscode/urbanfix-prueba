package pe.upc.urbanfix;

import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;
import pe.upc.urbanfix.notificaciones.*;
import pe.upc.urbanfix.nucleo.RegistroAuditoria;

public class NotificacionesYAuditoriaTest {

    @Test public void PU09_siEnviaALaPrimeraNoReintenta() {
        AtomicInteger llamadas = new AtomicInteger();
        SmtpClient ok = (d, a, m) -> llamadas.incrementAndGet();
        new NotificationService(List.of(new EmailAdapter(ok)), "r@x.pe").actualizar(inc(1, 1, "x"), "REPORTADO", "u");
        assertEquals(1, llamadas.get());
    }

    @Test public void PU09_elMensajeLlevaElCodigoDelTicketYElEvento() {
        List<String> destinos = new ArrayList<>(), mensajes = new ArrayList<>();
        SmtpClient smtp = (d, a, m) -> { destinos.add(d); mensajes.add(m); };
        new NotificationService(List.of(new EmailAdapter(smtp)), "residente@x.pe").actualizar(inc(5, 1, "x"), "ASIGNADO", "u");
        assertEquals(List.of("residente@x.pe"), destinos);
        assertEquals(List.of("Ticket UF-5: ASIGNADO"), mensajes);
    }

    @Test public void PU09_unCanalCaidoNoImpideNotificarPorLosDemas() {
        AtomicInteger intentosCaido = new AtomicInteger(), enviosOk = new AtomicInteger();
        SmtpClient caido = (d, a, m) -> { intentosCaido.incrementAndGet(); throw new ProveedorException(); };
        SmtpClient ok = (d, a, m) -> enviosOk.incrementAndGet();
        new NotificationService(List.of(new EmailAdapter(caido), new EmailAdapter(ok)), "r@x.pe")
                .actualizar(inc(1, 1, "x"), "REPORTADO", "u");
        assertEquals(3, intentosCaido.get());
        assertEquals(1, enviosOk.get());
    }

    @Test public void PU09_emailAdapterDevuelveTrueSiEnviaYFalseSiElProveedorFalla() {
        assertTrue(new EmailAdapter((d, a, m) -> { }).enviar("a@x.pe", "hola"));
        assertFalse(new EmailAdapter((d, a, m) -> { throw new ProveedorException(); }).enviar("a@x.pe", "hola"));
    }

    @Test public void PU10_unaTransicionInvalidaNoAuditaNiNotifica() {
        RegistroAuditoria auditoria = new RegistroAuditoria();
        List<String> correos = new ArrayList<>();
        NotificationService notif = new NotificationService(List.of(new EmailAdapter((d, a, m) -> correos.add(m))), "r@x.pe");
        IncidenciaService servicio = new IncidenciaService(List.of(auditoria, notif));
        Incidencia i = inc(1, 1, "Gasfitería");
        assertTransicion(() -> servicio.cerrar(i, 5, "res@x.pe"));
        assertTrue(auditoria.getRegistros().isEmpty());
        assertTrue(correos.isEmpty());
        assertEquals(Estado.REPORTADO, i.getEstadoActual().codigo());
    }

    @Test public void PU10_cadaTransicionRegistraUsuarioYEventoEnOrden() {
        RegistroAuditoria auditoria = new RegistroAuditoria();
        IncidenciaService s = new IncidenciaService(List.of(auditoria));
        Incidencia i = inc(3, 1, "Gasfitería"); Tecnico t = tec(1, "Gasfitería", 0);
        s.asignar(i, t, "admin@x.pe"); s.iniciar(i, t, "tec@x.pe");
        i.agregarEvidencia("foto.jpg");
        s.resolver(i, "reparado", "tec@x.pe"); s.cerrar(i, 4, "res@x.pe");
        List<String> eventos = auditoria.getRegistros().stream().map(r -> r.evento()).toList();
        assertEquals(List.of("ASIGNADO", "EN_ATENCION", "RESUELTO", "CERRADO"), eventos);
        assertEquals("res@x.pe", auditoria.getRegistros().get(3).usuario());
        assertEquals("Incidencia", auditoria.getRegistros().get(0).entidad());
    }

    @Test public void PU10_elServicioFuncionaSinObservadores() {
        Incidencia i = inc(1, 1, "Gasfitería");
        new IncidenciaService(List.of()).asignar(i, tec(1, "Gasfitería", 0), "admin");
        assertEquals(Estado.ASIGNADO, i.getEstadoActual().codigo());
    }
}
