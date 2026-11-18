package pe.upc.urbanfix.incidencias.domain;
import java.util.*;
public class IncidenciaRepository {
    private final Map<Long, Incidencia> datos = new HashMap<>();
    public void save(Incidencia i) { datos.put(i.getId(), i); }
    public long siguienteId() { return datos.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1; }
    public Optional<Incidencia> buscarPorIdYCondominio(long id, long condominioId) {
        return Optional.ofNullable(datos.get(id)).filter(i -> i.getCondominioId() == condominioId);
    }
    public Optional<Incidencia> buscarAbierta(long condominioId, String categoria, String ubicacion) {
        return datos.values().stream()
                .filter(i -> i.getCondominioId() == condominioId)
                .filter(i -> i.getCategoria().nombre().equalsIgnoreCase(categoria))
                .filter(i -> i.getUbicacion().equalsIgnoreCase(ubicacion))
                .filter(i -> i.getEstadoActual().codigo() != Estado.CERRADO)
                .findFirst();
    }
    public List<Incidencia> listarPorCondominio(long condominioId) {
        return datos.values().stream().filter(i -> i.getCondominioId() == condominioId).toList();
    }
}
