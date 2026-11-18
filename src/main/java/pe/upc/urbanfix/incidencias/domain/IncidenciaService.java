package pe.upc.urbanfix.incidencias.domain;
import java.util.List;
import pe.upc.urbanfix.autenticacion.Tecnico;
public class IncidenciaService {
    private final List<IObservadorIncidencia> observadores;
    private final IncidenciaRepository repositorio;
    public IncidenciaService(List<IObservadorIncidencia> o) { this(o, new IncidenciaRepository()); }
    public IncidenciaService(List<IObservadorIncidencia> o, IncidenciaRepository repositorio) {
        this.observadores = o; this.repositorio = repositorio;
    }
    public Incidencia registrar(RegistrarIncidenciaComando c) {
        repositorio.buscarAbierta(c.condominioId(), c.categoria().nombre(), c.ubicacion())
                .filter(abierta -> !c.confirmaDuplicado())
                .ifPresent(abierta -> { throw new PosibleDuplicadoException(abierta.getCodigo()); });
        long id = repositorio.siguienteId();
        Incidencia i = new Incidencia(id, String.format("UF-%04d", id), c.condominioId(), c.categoria(), c.ubicacion());
        repositorio.save(i);
        notificar(i, "REPORTADO", c.actor());
        return i;
    }
    public Incidencia asignar(Incidencia i, Tecnico t, String actor) {
        i.setEstadoActual(i.getEstadoActual().asignarTecnico(i, t)); notificar(i, "ASIGNADO", actor); return i;
    }
    public Incidencia iniciar(Incidencia i, Tecnico t, String actor) {
        i.setEstadoActual(i.getEstadoActual().iniciarAtencion(i, t)); notificar(i, "EN_ATENCION", actor); return i;
    }
    public Incidencia resolver(Incidencia i, String comentario, String actor) {
        i.setEstadoActual(i.getEstadoActual().resolver(i, comentario)); notificar(i, "RESUELTO", actor); return i;
    }
    public Incidencia cerrar(Incidencia i, int calificacion, String actor) {
        i.setEstadoActual(i.getEstadoActual().cerrar(i, calificacion)); notificar(i, "CERRADO", actor); return i;
    }
    private void notificar(Incidencia i, String evento, String actor) { observadores.forEach(o -> o.actualizar(i, evento, actor)); }
}
