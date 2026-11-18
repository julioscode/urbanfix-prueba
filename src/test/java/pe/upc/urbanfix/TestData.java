package pe.upc.urbanfix;
import pe.upc.urbanfix.autenticacion.Tecnico;
import pe.upc.urbanfix.incidencias.domain.*;
public final class TestData {
    private TestData() {}
    public static Incidencia inc(long id, long cond, String esp) { return new Incidencia(id, "UF-" + id, cond, new Categoria("cat", esp)); }
    public static Tecnico tec(long id, String esp, int carga) { return new Tecnico(id, esp, true, carga); }
    public static void assertTransicion(Runnable r) {
        try { r.run(); org.junit.Assert.fail("Debía lanzar TransicionInvalidaException"); }
        catch (TransicionInvalidaException e) { }
    }
}
