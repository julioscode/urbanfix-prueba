package pe.upc.urbanfix;
import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;
import java.util.List;
import org.junit.Test;
import pe.upc.urbanfix.incidencias.domain.*;
public class AsignacionStrategyTest {
    @Test public void PU03_asignaTecnicoConEspecialidadYMenorCarga() {
        var tecnicos = List.of(tec(1, "Gasfitería", 2), tec(2, "Gasfitería", 1), tec(3, "Electricidad", 0));
        var elegido = new AsignacionMenorCargaStrategy().elegirTecnico(inc(1, 1, "Gasfitería"), tecnicos).orElseThrow();
        assertEquals(2L, elegido.getId());
        assertTrue(new AsignacionMenorCargaStrategy().elegirTecnico(inc(2, 1, "Pintura"), tecnicos).isEmpty());
    }
}
