package pe.upc.urbanfix;
import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;
import java.util.List;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;
public class EstadoIncidenciaTest {
    @Test public void PU01_resolverSinEvidenciaOSinDetalleEsRechazado() {
        Incidencia i = inc(1, 1, "Gasfitería"); i.setEstadoActual(new EstadoEnAtencion());
        assertTransicion(() -> i.getEstadoActual().resolver(i, "reparado"));
        i.agregarEvidencia("foto1.jpg");
        assertTransicion(() -> i.getEstadoActual().resolver(i, " "));
        assertEquals(Estado.RESUELTO, i.getEstadoActual().resolver(i, "reparado").codigo());
    }
    @Test public void PU02_soloTransicionesConsecutivas() {
        Incidencia i = inc(1, 1, "Gasfitería"); Tecnico t = tec(1, "Gasfitería", 0);
        assertTransicion(() -> i.getEstadoActual().cerrar(i, 5));
        assertTransicion(() -> i.getEstadoActual().iniciarAtencion(i, t));
        IncidenciaService s = new IncidenciaService(List.of());
        s.asignar(i, t, "admin"); assertEquals(Estado.ASIGNADO, i.getEstadoActual().codigo());
        s.iniciar(i, t, "tec"); assertEquals(Estado.EN_ATENCION, i.getEstadoActual().codigo());
        i.agregarEvidencia("foto.jpg");
        s.resolver(i, "reparado", "tec"); assertEquals(Estado.RESUELTO, i.getEstadoActual().codigo());
        s.cerrar(i, 5, "res"); assertEquals(Estado.CERRADO, i.getEstadoActual().codigo());
        assertTransicion(() -> i.getEstadoActual().resolver(i, "otra vez"));
    }
}
