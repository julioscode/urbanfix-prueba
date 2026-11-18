package pe.upc.urbanfix.autenticacion;
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() { super("Credenciales inválidas"); }
}
