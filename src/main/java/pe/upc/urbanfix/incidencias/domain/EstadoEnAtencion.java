package pe.upc.urbanfix.incidencias.domain;
public final class EstadoEnAtencion implements IEstadoIncidencia {
    public Estado codigo() { return Estado.EN_ATENCION; }
    @Override public IEstadoIncidencia resolver(Incidencia i, String comentario) {
        if (i.getEvidencias().isEmpty() || comentario == null || comentario.isBlank())
            throw new TransicionInvalidaException("Resolver exige evidencia y detalle");
        return new EstadoResuelto();
    }
}
