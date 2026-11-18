package pe.upc.urbanfix.notificaciones;
public interface SmtpClient { void send(String destino, String asunto, String mensaje) throws ProveedorException; }
