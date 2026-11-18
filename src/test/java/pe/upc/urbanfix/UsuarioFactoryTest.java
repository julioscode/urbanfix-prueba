package pe.upc.urbanfix;
import static org.junit.Assert.*;
import org.junit.Test;
import pe.upc.urbanfix.autenticacion.*;
public class UsuarioFactoryTest {
    @Test public void PU06_fabricaDevuelveLaSubclaseCorrectaPorRol() {
        UsuarioFactory f = new UsuarioFactory();
        DatosUsuario d = new DatosUsuario("Ana", "ana@x.pe", 1, "301", "A", "noche", "Gasfitería", true);
        assertTrue(f.crearUsuario(Rol.RESIDENTE, d) instanceof Residente);
        assertTrue(f.crearUsuario(Rol.CONSERJE, d) instanceof Conserje);
        assertTrue(f.crearUsuario(Rol.TECNICO, d) instanceof Tecnico);
        assertTrue(f.crearUsuario(Rol.ADMINISTRADOR, d) instanceof Administrador);
        assertEquals(1L, f.crearUsuario(Rol.RESIDENTE, d).getCondominioId());
    }
}
