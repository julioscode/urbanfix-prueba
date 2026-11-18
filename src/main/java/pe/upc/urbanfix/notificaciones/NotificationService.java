package pe.upc.urbanfix.notificaciones;
import java.util.List;
import pe.upc.urbanfix.incidencias.domain.*;
public class NotificationService implements IObservadorIncidencia {
    static final int MAX_INTENTOS = 3;
    private final List<INotificacionAdapter> canales; private final String destino;
    public NotificationService(List<INotificacionAdapter> canales, String destino) { this.canales = canales; this.destino = destino; }
    @Override public void actualizar(Incidencia i, String evento, String usuario) {
        String msg = "Ticket " + i.getCodigo() + ": " + evento;
        canales.forEach(c -> enviarConReintento(c, destino, msg));
    }
    private void enviarConReintento(INotificacionAdapter canal, String destino, String msg) {
        for (int n = 1; n <= MAX_INTENTOS; n++) if (canal.enviar(destino, msg)) return;
    }
}
