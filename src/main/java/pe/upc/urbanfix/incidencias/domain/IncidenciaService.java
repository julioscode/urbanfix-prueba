package pe.upc.urbanfix.incidencias.domain;
import java.util.List;
import pe.upc.urbanfix.autenticacion.Tecnico;
public class IncidenciaService {
    private final List<IObservadorIncidencia> observadores;
    public IncidenciaService(List<IObservadorIncidencia> o) { this.observadores = o; }
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
