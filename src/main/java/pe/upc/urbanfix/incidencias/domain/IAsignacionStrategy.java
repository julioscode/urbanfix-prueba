package pe.upc.urbanfix.incidencias.domain;
import java.util.List; import java.util.Optional;
import pe.upc.urbanfix.autenticacion.Tecnico;
public interface IAsignacionStrategy { Optional<Tecnico> elegirTecnico(Incidencia incidencia, List<Tecnico> tecnicos); }
