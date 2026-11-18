package pe.upc.urbanfix;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.*;

public class AutenticacionServiceTest {

    private AutenticacionService servicio;
    private Usuario luis;

    @Before public void preparar() {
        servicio = new AutenticacionService();
        luis = new UsuarioFactory().crearUsuario(Rol.RESIDENTE,
                new DatosUsuario("Luis", "Luis@Cond.pe", 10, "301", "A", "", "", false));
        servicio.registrar(luis, "clave-segura-1");
    }

    @Test public void PU13_loginCorrectoDevuelveUnTokenQueIdentificaAlUsuario() {
        String token = servicio.login("luis@cond.pe", "clave-segura-1");
        assertFalse(token.isBlank());
        assertSame(luis, servicio.usuarioDe(token).orElseThrow());
    }

    @Test public void PU13_elCorreoSeComparaSinDistinguirMayusculas() {
        assertNotNull(servicio.login("LUIS@COND.PE", "clave-segura-1"));
    }

    @Test(expected = CredencialesInvalidasException.class)
    public void PU13_contrasenaIncorrectaEsRechazada() {
        servicio.login("luis@cond.pe", "otra-clave-123");
    }

    @Test(expected = CredencialesInvalidasException.class)
    public void PU13_usuarioInexistenteEsRechazado() {
        servicio.login("nadie@cond.pe", "clave-segura-1");
    }

    @Test(expected = CredencialesInvalidasException.class)
    public void PU13_credencialesNulasSonRechazadas() {
        servicio.login(null, null);
    }

    @Test public void PU13_cadaLoginGeneraUnTokenDistinto() {
        assertNotEquals(servicio.login("luis@cond.pe", "clave-segura-1"), servicio.login("luis@cond.pe", "clave-segura-1"));
    }

    @Test public void PU13_cerrarSesionInvalidaElToken() {
        String token = servicio.login("luis@cond.pe", "clave-segura-1");
        servicio.cerrarSesion(token);
        assertTrue(servicio.usuarioDe(token).isEmpty());
    }

    @Test public void PU13_unTokenDesconocidoONuloNoIdentificaANadie() {
        assertTrue(servicio.usuarioDe("falso").isEmpty());
        assertTrue(servicio.usuarioDe(null).isEmpty());
        servicio.cerrarSesion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void PU13_laContrasenaDebeTenerAlMenosOchoCaracteres() {
        servicio.registrar(new UsuarioFactory().crearUsuario(Rol.CONSERJE,
                new DatosUsuario("Ana", "ana@cond.pe", 10, "", "", "noche", "", false)), "corta");
    }

    @Test(expected = IllegalStateException.class)
    public void PU13_noSePermiteRegistrarDosVecesElMismoCorreo() {
        servicio.registrar(luis, "clave-segura-2");
    }
}
