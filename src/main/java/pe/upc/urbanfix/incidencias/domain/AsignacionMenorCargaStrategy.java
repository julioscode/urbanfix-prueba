package pe.upc.urbanfix.incidencias.domain;
import java.util.Comparator; import java.util.List; import java.util.Optional;
import pe.upc.urbanfix.autenticacion.Tecnico;
public class AsignacionMenorCargaStrategy implements IAsignacionStrategy {
    @Override public Optional<Tecnico> elegirTecnico(Incidencia inc, List<Tecnico> tecnicos) {
        return tecnicos.stream().filter(Tecnico::isDisponible)
            .filter(t -> t.getEspecialidad().equalsIgnoreCase(inc.getCategoria().getEspecialidadRequerida()))
            .min(Comparator.comparingInt(Tecnico::getCargaActiva).thenComparingLong(Tecnico::getId));
    }
}
