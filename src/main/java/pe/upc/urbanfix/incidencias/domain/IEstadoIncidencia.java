package pe.upc.urbanfix.incidencias.domain;
import pe.upc.urbanfix.autenticacion.Tecnico;
public interface IEstadoIncidencia {
    Estado codigo();
    default IEstadoIncidencia asignarTecnico(Incidencia i, Tecnico t) { throw invalida("asignar"); }
    default IEstadoIncidencia iniciarAtencion(Incidencia i, Tecnico a) { throw invalida("iniciar"); }
    default IEstadoIncidencia resolver(Incidencia i, String comentario) { throw invalida("resolver"); }
    default IEstadoIncidencia cerrar(Incidencia i, Integer calificacion) { throw invalida("cerrar"); }
    private TransicionInvalidaException invalida(String accion) {
        return new TransicionInvalidaException("No se puede " + accion + " desde " + codigo());
    }
}
