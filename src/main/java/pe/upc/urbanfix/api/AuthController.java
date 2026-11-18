package pe.upc.urbanfix.api;
import java.util.Map;
import pe.upc.urbanfix.autenticacion.AutenticacionService;
import pe.upc.urbanfix.autenticacion.CredencialesInvalidasException;
public class AuthController {
    private final AutenticacionService autenticacion;
    public AuthController(AutenticacionService autenticacion) { this.autenticacion = autenticacion; }
    public Respuesta login(String correo, String contrasena) {
        try {
            return new Respuesta(200, Map.of("token", autenticacion.login(correo, contrasena)));
        } catch (CredencialesInvalidasException e) {
            return new Respuesta(401, Map.of("detalle", e.getMessage()));
        }
    }
}
