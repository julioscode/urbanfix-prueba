package pe.upc.urbanfix.incidencias.domain;
public class PosibleDuplicadoException extends RuntimeException {
    private final String codigoExistente;
    public PosibleDuplicadoException(String codigoExistente) {
        super("Posible duplicado del ticket " + codigoExistente);
        this.codigoExistente = codigoExistente;
    }
    public String getCodigoExistente() { return codigoExistente; }
}
