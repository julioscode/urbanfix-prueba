package pe.upc.urbanfix.nucleo;
import java.time.Instant; import java.util.*;
import pe.upc.urbanfix.incidencias.domain.*;
public class RegistroAuditoria implements IObservadorIncidencia {
    public record Registro(String usuario, String evento, String entidad, long entidadId, Instant marca) {}
    private final List<Registro> registros = new ArrayList<>();
    @Override public void actualizar(Incidencia i, String evento, String usuario) {
        registros.add(new Registro(usuario, evento, "Incidencia", i.getId(), Instant.now()));
    }
    public List<Registro> getRegistros() { return registros; }
}
