package pe.upc.urbanfix.incidencias.domain;
import java.util.ArrayList;
import java.util.List;
import pe.upc.urbanfix.autenticacion.Tecnico;
public class Incidencia {
    private final long id; private final String codigo; private final long condominioId;
    private final Categoria categoria; private final List<String> evidencias = new ArrayList<>();
    private IEstadoIncidencia estadoActual = new EstadoReportado();
    private Tecnico tecnico;
    public Incidencia(long id, String codigo, long condominioId, Categoria categoria) {
        this.id = id; this.codigo = codigo; this.condominioId = condominioId; this.categoria = categoria;
    }
    public void asignarA(Tecnico t) { this.tecnico = t; t.incrementarCarga(); }
    public void agregarEvidencia(String e) { evidencias.add(e); }
    public long getId() { return id; } public String getCodigo() { return codigo; }
    public long getCondominioId() { return condominioId; } public Categoria getCategoria() { return categoria; }
    public List<String> getEvidencias() { return evidencias; } public Tecnico getTecnico() { return tecnico; }
    public IEstadoIncidencia getEstadoActual() { return estadoActual; }
    public void setEstadoActual(IEstadoIncidencia e) { this.estadoActual = e; }
}
