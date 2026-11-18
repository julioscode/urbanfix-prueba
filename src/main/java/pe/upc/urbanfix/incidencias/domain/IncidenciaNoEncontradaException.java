package pe.upc.urbanfix.incidencias.domain;
public class IncidenciaNoEncontradaException extends RuntimeException {
    public IncidenciaNoEncontradaException() { super("Incidencia no encontrada"); }
}
