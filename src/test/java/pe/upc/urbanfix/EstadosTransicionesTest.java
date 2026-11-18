package pe.upc.urbanfix;

import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;

import org.junit.Test;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;

/** PU-07: cada estado del patrón State solo acepta su transición válida. */
public class EstadosTransicionesTest {

    private final Tecnico tecnico = tec(1, "Gasfitería", 0);

    @Test public void PU07_reportadoSoloAceptaAsignar() {
        Incidencia i = inc(1, 1, "Gasfitería");
        IEstadoIncidencia e = new EstadoReportado();
        assertTransicion(() -> e.iniciarAtencion(i, tecnico));
        assertTransicion(() -> e.resolver(i, "x"));
        assertTransicion(() -> e.cerrar(i, 5));
        assertEquals(Estado.ASIGNADO, e.asignarTecnico(i, tecnico).codigo());
    }

    @Test public void PU07_asignadoSoloAceptaIniciarAtencion() {
        Incidencia i = inc(1, 1, "Gasfitería");
        IEstadoIncidencia e = new EstadoAsignado();
        assertTransicion(() -> e.asignarTecnico(i, tecnico));
        assertTransicion(() -> e.resolver(i, "x"));
        assertTransicion(() -> e.cerrar(i, 5));
        assertEquals(Estado.EN_ATENCION, e.iniciarAtencion(i, tecnico).codigo());
    }

    @Test public void PU07_enAtencionSoloAceptaResolver() {
        Incidencia i = inc(1, 1, "Gasfitería");
        i.agregarEvidencia("foto.jpg");
        IEstadoIncidencia e = new EstadoEnAtencion();
        assertTransicion(() -> e.asignarTecnico(i, tecnico));
        assertTransicion(() -> e.iniciarAtencion(i, tecnico));
        assertTransicion(() -> e.cerrar(i, 5));
        assertEquals(Estado.RESUELTO, e.resolver(i, "reparado").codigo());
    }

    @Test public void PU07_resueltoSoloAceptaCerrarConCalificacionValida() {
        Incidencia i = inc(1, 1, "Gasfitería");
        IEstadoIncidencia e = new EstadoResuelto();
        assertTransicion(() -> e.asignarTecnico(i, tecnico));
        assertTransicion(() -> e.iniciarAtencion(i, tecnico));
        assertTransicion(() -> e.resolver(i, "x"));
        assertTransicion(() -> e.cerrar(i, null));
        assertTransicion(() -> e.cerrar(i, 0));
        assertTransicion(() -> e.cerrar(i, 6));
        assertEquals(Estado.CERRADO, e.cerrar(i, 1).codigo());
        assertEquals(Estado.CERRADO, e.cerrar(i, 5).codigo());
    }

    @Test public void PU07_cerradoEsTerminalYNoAceptaNada() {
        Incidencia i = inc(1, 1, "Gasfitería");
        IEstadoIncidencia e = new EstadoCerrado();
        assertTransicion(() -> e.asignarTecnico(i, tecnico));
        assertTransicion(() -> e.iniciarAtencion(i, tecnico));
        assertTransicion(() -> e.resolver(i, "x"));
        assertTransicion(() -> e.cerrar(i, 5));
    }

    @Test public void PU07_elMensajeIndicaLaAccionYElEstado() {
        Incidencia i = inc(1, 1, "Gasfitería");
        try { new EstadoReportado().cerrar(i, 5); fail("Debía lanzar TransicionInvalidaException"); }
        catch (TransicionInvalidaException e) {
            assertTrue(e.getMessage().contains("cerrar"));
            assertTrue(e.getMessage().contains("REPORTADO"));
        }
    }

    @Test public void PU07_resolverConDetalleNuloEsRechazado() {
        Incidencia i = inc(1, 1, "Gasfitería");
        i.agregarEvidencia("foto.jpg");
        assertTransicion(() -> new EstadoEnAtencion().resolver(i, null));
    }
}
