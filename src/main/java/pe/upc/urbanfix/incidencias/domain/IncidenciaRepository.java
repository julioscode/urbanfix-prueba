package pe.upc.urbanfix.incidencias.domain;
import java.util.*;
public class IncidenciaRepository {
    private final Map<Long, Incidencia> datos = new HashMap<>();
    public void save(Incidencia i) { datos.put(i.getId(), i); }
    public Optional<Incidencia> buscarPorIdYCondominio(long id, long condominioId) {
        return Optional.ofNullable(datos.get(id)).filter(i -> i.getCondominioId() == condominioId);
    }
    public List<Incidencia> listarPorCondominio(long condominioId) {
        return datos.values().stream().filter(i -> i.getCondominioId() == condominioId).toList();
    }
}
