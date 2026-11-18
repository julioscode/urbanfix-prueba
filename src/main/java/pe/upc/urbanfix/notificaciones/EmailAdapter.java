package pe.upc.urbanfix.notificaciones;
public class EmailAdapter implements INotificacionAdapter {
    private final SmtpClient smtpClient;
    public EmailAdapter(SmtpClient c) { this.smtpClient = c; }
    @Override public boolean enviar(String destino, String mensaje) {
        try { smtpClient.send(destino, "UrbanFix", mensaje); return true; }
        catch (ProveedorException e) { return false; }
    }
}
