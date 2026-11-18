package pe.upc.urbanfix.incidencias.domain;
import pe.upc.urbanfix.autenticacion.Tecnico;
public final class EstadoAsignado implements IEstadoIncidencia {
    public Estado codigo() { return Estado.ASIGNADO; }
    @Override public IEstadoIncidencia iniciarAtencion(Incidencia i, Tecnico a) { return new EstadoEnAtencion(); }
}
