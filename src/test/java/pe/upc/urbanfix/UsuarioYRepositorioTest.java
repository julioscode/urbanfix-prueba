package pe.upc.urbanfix;

import static org.junit.Assert.*;
import static pe.upc.urbanfix.TestData.*;

import org.junit.Test;
import pe.upc.urbanfix.autenticacion.*;
import pe.upc.urbanfix.incidencias.domain.IncidenciaRepository;

public class UsuarioYRepositorioTest {

    private final DatosUsuario datos = new DatosUsuario("Ana", "ana@x.pe", 7, "301", "A", "noche", "Gasfitería", true);

    @Test public void PU11_todosLosRolesQuedanInicializadosConSusDatosComunes() {
        UsuarioFactory f = new UsuarioFactory();
        for (Rol rol : Rol.values()) {
            Usuario u = f.crearUsuario(rol, datos);
            assertEquals("Ana", u.getNombre());
            assertEquals("ana@x.pe", u.getCorreo());
            assertEquals(7L, u.getCondominioId());
        }
    }

    @Test public void PU11_elTecnicoConservaEspecialidadYDisponibilidad() {
        Tecnico t = (Tecnico) new UsuarioFactory().crearUsuario(Rol.TECNICO, datos);
        assertEquals("Gasfitería", t.getEspecialidad());
        assertTrue(t.isDisponible());
        assertEquals(0, t.getCargaActiva());
    }

    @Test public void PU12_buscarUnTicketInexistenteDevuelveVacio() {
        assertTrue(new IncidenciaRepository().buscarPorIdYCondominio(99, 10).isEmpty());
    }

    @Test public void PU12_listarPorCondominioSoloDevuelveLosDeEseCondominio() {
        IncidenciaRepository repo = new IncidenciaRepository();
        repo.save(inc(1, 10, "x")); repo.save(inc(2, 10, "x")); repo.save(inc(3, 20, "x"));
        assertEquals(2, repo.listarPorCondominio(10).size());
        assertEquals(1, repo.listarPorCondominio(20).size());
        assertTrue(repo.listarPorCondominio(30).isEmpty());
    }
}
