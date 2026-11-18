package pe.upc.urbanfix;

import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;

import java.util.List;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;

public class AsignacionReglasTest {

    private final AsignacionMenorCargaStrategy estrategia = new AsignacionMenorCargaStrategy();

    @Test public void PU08_ignoraTecnicosNoDisponibles() {
        var tecnicos = List.of(new Tecnico(1, "Gasfitería", false, 0), new Tecnico(2, "Gasfitería", true, 5));
        assertEquals(2L, estrategia.elegirTecnico(inc(1, 1, "Gasfitería"), tecnicos).orElseThrow().getId());
    }

    @Test public void PU08_conMismaCargaDesempataPorIdMenor() {
        var tecnicos = List.of(tec(7, "Gasfitería", 1), tec(4, "Gasfitería", 1), tec(9, "Gasfitería", 1));
        assertEquals(4L, estrategia.elegirTecnico(inc(1, 1, "Gasfitería"), tecnicos).orElseThrow().getId());
    }

    @Test public void PU08_laEspecialidadSeComparaSinDistinguirMayusculas() {
        var tecnicos = List.of(tec(1, "GASFITERÍA", 0));
        assertTrue(estrategia.elegirTecnico(inc(1, 1, "gasfitería"), tecnicos).isPresent());
    }

    @Test public void PU08_sinTecnicosODisponiblesDevuelveVacio() {
        assertTrue(estrategia.elegirTecnico(inc(1, 1, "Gasfitería"), List.of()).isEmpty());
        var noDisponibles = List.of(new Tecnico(1, "Gasfitería", false, 0));
        assertTrue(estrategia.elegirTecnico(inc(1, 1, "Gasfitería"), noDisponibles).isEmpty());
    }

    @Test public void PU08_asignarIncrementaLaCargaYGuardaElTecnico() {
        Incidencia i = inc(1, 1, "Gasfitería"); Tecnico t = tec(1, "Gasfitería", 2);
        new IncidenciaService(List.of()).asignar(i, t, "admin@x.pe");
        assertEquals(3, t.getCargaActiva());
        assertSame(t, i.getTecnico());
    }

    @Test public void PU08_tras_asignar_otroTecnicoPasaAserMasConveniente() {
        Tecnico a = tec(1, "Gasfitería", 0), b = tec(2, "Gasfitería", 0);
        IncidenciaService s = new IncidenciaService(List.of());
        Incidencia i1 = inc(1, 1, "Gasfitería"), i2 = inc(2, 1, "Gasfitería");
        s.asignar(i1, estrategia.elegirTecnico(i1, List.of(a, b)).orElseThrow(), "admin");
        assertEquals(1, a.getCargaActiva());
        assertEquals(b, estrategia.elegirTecnico(i2, List.of(a, b)).orElseThrow());
    }
}
