package pe.upc.urbanfix;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import pe.upc.urbanfix.incidencias.domain.*;
import pe.upc.urbanfix.nucleo.RegistroAuditoria;

public class RegistroDuplicadosTest {

    private IncidenciaRepository repositorio;
    private RegistroAuditoria auditoria;
    private IncidenciaService servicio;
    private final Categoria fuga = new Categoria("Fuga de agua", "Gasfitería");

    @Before public void preparar() {
        repositorio = new IncidenciaRepository();
        auditoria = new RegistroAuditoria();
        servicio = new IncidenciaService(List.of(auditoria), repositorio);
    }

    private RegistrarIncidenciaComando comando(long condominio, String ubicacion, boolean confirma) {
        return new RegistrarIncidenciaComando(condominio, fuga, ubicacion, confirma, "luis@cond.pe");
    }

    @Test public void PU15_registrarCreaUnTicketReportadoConCodigoYAuditoria() {
        Incidencia i = servicio.registrar(comando(10, "Torre A piso 3", false));
        assertEquals("UF-0001", i.getCodigo());
        assertEquals(Estado.REPORTADO, i.getEstadoActual().codigo());
        assertEquals("Torre A piso 3", i.getUbicacion());
        assertTrue(repositorio.buscarPorIdYCondominio(i.getId(), 10).isPresent());
        assertEquals("REPORTADO", auditoria.getRegistros().get(0).evento());
        assertEquals("luis@cond.pe", auditoria.getRegistros().get(0).usuario());
    }

    @Test public void PU15_losCodigosSonCorrelativos() {
        assertEquals("UF-0001", servicio.registrar(comando(10, "A", false)).getCodigo());
        assertEquals("UF-0002", servicio.registrar(comando(10, "B", false)).getCodigo());
    }

    @Test public void PU15_unPosibleDuplicadoSeRechazaSiNoSeConfirma() {
        Incidencia primera = servicio.registrar(comando(10, "Torre A piso 3", false));
        try {
            servicio.registrar(comando(10, "torre a piso 3", false));
            fail("Debía lanzar PosibleDuplicadoException");
        } catch (PosibleDuplicadoException e) {
            assertEquals(primera.getCodigo(), e.getCodigoExistente());
        }
        assertEquals(1, repositorio.listarPorCondominio(10).size());
    }

    @Test public void PU15_unDuplicadoConfirmadoSiSeRegistra() {
        servicio.registrar(comando(10, "Torre A piso 3", false));
        Incidencia segunda = servicio.registrar(comando(10, "Torre A piso 3", true));
        assertEquals("UF-0002", segunda.getCodigo());
        assertEquals(2, repositorio.listarPorCondominio(10).size());
    }

    @Test public void PU15_otraUbicacionOCategoriaNoEsDuplicado() {
        servicio.registrar(comando(10, "Torre A piso 3", false));
        servicio.registrar(comando(10, "Torre B piso 1", false));
        servicio.registrar(new RegistrarIncidenciaComando(10, new Categoria("Corto circuito", "Electricidad"),
                "Torre A piso 3", false, "luis@cond.pe"));
        assertEquals(3, repositorio.listarPorCondominio(10).size());
    }

    @Test public void PU15_unTicketCerradoNoCuentaComoDuplicado() {
        Incidencia primera = servicio.registrar(comando(10, "Torre A piso 3", false));
        primera.setEstadoActual(new EstadoCerrado());
        servicio.registrar(comando(10, "Torre A piso 3", false));
        assertEquals(2, repositorio.listarPorCondominio(10).size());
    }

    @Test public void PU15_otroCondominioNoCuentaComoDuplicado() {
        servicio.registrar(comando(10, "Torre A piso 3", false));
        servicio.registrar(comando(20, "Torre A piso 3", false));
        assertEquals(1, repositorio.listarPorCondominio(10).size());
        assertEquals(1, repositorio.listarPorCondominio(20).size());
    }

    @Test public void PU15_unDuplicadoRechazadoNoNotificaNiAudita() {
        List<String> eventos = new ArrayList<>();
        IncidenciaService conObservador = new IncidenciaService(List.of((i, e, u) -> eventos.add(e)), repositorio);
        conObservador.registrar(comando(10, "A", false));
        try { conObservador.registrar(comando(10, "A", false)); } catch (PosibleDuplicadoException e) { }
        assertEquals(List.of("REPORTADO"), eventos);
    }

    @Test public void PU15_siguienteIdContinuaDespuesDelMayorGuardado() {
        repositorio.save(new Incidencia(7, "UF-0007", 10, fuga, "X"));
        assertEquals(8L, repositorio.siguienteId());
    }
}
