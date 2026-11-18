package pe.upc.urbanfix;
import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;
import org.junit.Test;
import pe.upc.urbanfix.incidencias.domain.IncidenciaRepository;
public class AislamientoCondominioTest {
    @Test public void PI01_unUsuarioNoAccedeATicketsDeOtroCondominio() {
        IncidenciaRepository repo = new IncidenciaRepository();
        repo.save(inc(1, 10, "Gasfitería")); repo.save(inc(2, 20, "Gasfitería"));
        assertTrue(repo.buscarPorIdYCondominio(1, 10).isPresent());
        assertTrue(repo.buscarPorIdYCondominio(1, 20).isEmpty());
        assertEquals(1, repo.listarPorCondominio(10).size());
    }
}
