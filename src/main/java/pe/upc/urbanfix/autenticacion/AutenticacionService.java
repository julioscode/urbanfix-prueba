package pe.upc.urbanfix.autenticacion;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
public class AutenticacionService {
    private static final int LONGITUD_MINIMA = 8;
    private static final int ITERACIONES = 65536;
    private record Cuenta(Usuario usuario, byte[] sal, byte[] hash) {}
    private final Map<String, Cuenta> cuentas = new HashMap<>();
    private final Map<String, Usuario> sesiones = new HashMap<>();
    private final SecureRandom azar = new SecureRandom();
    public void registrar(Usuario usuario, String contrasena) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA)
            throw new IllegalArgumentException("La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres");
        String correo = usuario.getCorreo().toLowerCase();
        if (cuentas.containsKey(correo)) throw new IllegalStateException("Ya existe un usuario con ese correo");
        byte[] sal = new byte[16];
        azar.nextBytes(sal);
        cuentas.put(correo, new Cuenta(usuario, sal, derivar(sal, contrasena)));
    }
    public String login(String correo, String contrasena) {
        Cuenta cuenta = (correo == null || contrasena == null) ? null : cuentas.get(correo.toLowerCase());
        if (cuenta == null || !MessageDigest.isEqual(cuenta.hash(), derivar(cuenta.sal(), contrasena)))
            throw new CredencialesInvalidasException();
        String token = UUID.randomUUID().toString();
        sesiones.put(token, cuenta.usuario());
        return token;
    }
    public Optional<Usuario> usuarioDe(String token) { return Optional.ofNullable(token).map(sesiones::get); }
    public void cerrarSesion(String token) { if (token != null) sesiones.remove(token); }
    private byte[] derivar(byte[] sal, String contrasena) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, 256)).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
