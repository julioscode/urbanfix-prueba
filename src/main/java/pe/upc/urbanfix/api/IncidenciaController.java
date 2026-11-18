package pe.upc.urbanfix.api;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import pe.upc.urbanfix.autenticacion.*;
import pe.upc.urbanfix.incidencias.domain.*;
public class IncidenciaController {
    private final AutenticacionService autenticacion;
    private final Autorizador autorizador;
    private final IncidenciaService servicio;
    private final IncidenciaRepository repositorio;
    public IncidenciaController(AutenticacionService autenticacion, Autorizador autorizador,
                                IncidenciaService servicio, IncidenciaRepository repositorio) {
        this.autenticacion = autenticacion; this.autorizador = autorizador;
        this.servicio = servicio; this.repositorio = repositorio;
    }
    public Respuesta registrar(String token, Categoria categoria, String ubicacion, boolean confirmaDuplicado) {
        return ejecutar(token, Accion.REGISTRAR_INCIDENCIA, u -> new Respuesta(201,
                servicio.registrar(new RegistrarIncidenciaComando(u.getCondominioId(), categoria, ubicacion, confirmaDuplicado, u.getCorreo()))));
    }
    public Respuesta listar(String token) {
        return ejecutar(token, Accion.LISTAR_INCIDENCIAS, u -> new Respuesta(200, repositorio.listarPorCondominio(u.getCondominioId())));
    }
    public Respuesta cambiarEstado(String token, long id, String accion, String comentario) {
        return ejecutar(token, Accion.CAMBIAR_ESTADO, u -> {
            Incidencia i = buscar(id, u);
            Tecnico tecnico = (Tecnico) u;
            if (i.getTecnico() == null || i.getTecnico().getId() != tecnico.getId())
                throw new AccesoDenegadoException("El técnico no está asignado a este ticket");
            return switch (accion == null ? "" : accion) {
                case "INICIAR" -> new Respuesta(200, servicio.iniciar(i, tecnico, u.getCorreo()));
                case "RESOLVER" -> new Respuesta(200, servicio.resolver(i, comentario, u.getCorreo()));
                default -> new Respuesta(400, Map.of("detalle", "Acción desconocida: " + accion));
            };
        });
    }
    public Respuesta calificar(String token, long id, int calificacion) {
        return ejecutar(token, Accion.CALIFICAR_INCIDENCIA,
                u -> new Respuesta(200, servicio.cerrar(buscar(id, u), calificacion, u.getCorreo())));
    }
    private Incidencia buscar(long id, Usuario usuario) {
        return repositorio.buscarPorIdYCondominio(id, usuario.getCondominioId()).orElseThrow(IncidenciaNoEncontradaException::new);
    }
    private Respuesta ejecutar(String token, Accion accion, Function<Usuario, Respuesta> operacion) {
        Optional<Usuario> usuario = autenticacion.usuarioDe(token);
        if (usuario.isEmpty()) return error(401, "Sesión no válida");
        try {
            autorizador.exigir(usuario.get(), accion);
            return operacion.apply(usuario.get());
        } catch (AccesoDenegadoException e) {
            return error(403, e.getMessage());
        } catch (IncidenciaNoEncontradaException e) {
            return error(404, e.getMessage());
        } catch (PosibleDuplicadoException | TransicionInvalidaException e) {
            return error(409, e.getMessage());
        }
    }
    private Respuesta error(int estado, String detalle) { return new Respuesta(estado, Map.of("detalle", detalle)); }
}
