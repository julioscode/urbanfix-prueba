package pe.upc.urbanfix;
import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;
import pe.upc.urbanfix.notificaciones.*;
import pe.upc.urbanfix.nucleo.RegistroAuditoria;
public class ObserverAdapterTest {
    @Test public void PU04_cadaTransicionGeneraRegistroDeAuditoria() {
        RegistroAuditoria aud = new RegistroAuditoria(); IncidenciaService s = new IncidenciaService(List.of(aud));
        Incidencia i = inc(7, 1, "Gasfitería"); Tecnico t = tec(1, "Gasfitería", 0);
        s.asignar(i, t, "admin@x.pe"); s.iniciar(i, t, "tec@x.pe");
        assertEquals(2, aud.getRegistros().size());
        var r = aud.getRegistros().get(0);
        assertEquals("admin@x.pe", r.usuario()); assertEquals("ASIGNADO", r.evento());
        assertEquals(7L, r.entidadId()); assertNotNull(r.marca());
    }
    @Test public void PU05_fallosTransitoriosSeReintentanHastaTresVeces() {
        AtomicInteger llamadas = new AtomicInteger();
        SmtpClient falla2 = (d, a, m) -> { if (llamadas.incrementAndGet() <= 2) throw new ProveedorException(); };
        new NotificationService(List.of(new EmailAdapter(falla2)), "r@x.pe").actualizar(inc(1, 1, "x"), "REPORTADO", "u");
        assertEquals(3, llamadas.get());
        AtomicInteger siempre = new AtomicInteger();
        SmtpClient caido = (d, a, m) -> { siempre.incrementAndGet(); throw new ProveedorException(); };
        new NotificationService(List.of(new EmailAdapter(caido)), "r@x.pe").actualizar(inc(1, 1, "x"), "REPORTADO", "u");
        assertEquals(3, siempre.get());
    }
}
