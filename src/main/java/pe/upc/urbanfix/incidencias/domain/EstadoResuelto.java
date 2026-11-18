package pe.upc.urbanfix.incidencias.domain;
public final class EstadoResuelto implements IEstadoIncidencia {
    public Estado codigo() { return Estado.RESUELTO; }
    @Override public IEstadoIncidencia cerrar(Incidencia i, Integer calificacion) {
        if (calificacion == null || calificacion < 1 || calificacion > 5)
            throw new TransicionInvalidaException("La calificación debe estar entre 1 y 5");
        return new EstadoCerrado();
    }
}
