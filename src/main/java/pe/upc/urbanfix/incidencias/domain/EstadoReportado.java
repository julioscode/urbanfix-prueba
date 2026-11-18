package pe.upc.urbanfix.incidencias.domain;
import pe.upc.urbanfix.autenticacion.Tecnico;
public final class EstadoReportado implements IEstadoIncidencia {
    public Estado codigo() { return Estado.REPORTADO; }
    @Override public IEstadoIncidencia asignarTecnico(Incidencia i, Tecnico t) { i.asignarA(t); return new EstadoAsignado(); }
}
