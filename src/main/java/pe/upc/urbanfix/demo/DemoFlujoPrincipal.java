package pe.upc.urbanfix.demo;

import java.util.*;
import pe.upc.urbanfix.autenticacion.*;
import pe.upc.urbanfix.incidencias.domain.*;
import pe.upc.urbanfix.notificaciones.*;
import pe.upc.urbanfix.nucleo.RegistroAuditoria;
public class DemoFlujoPrincipal {
  static void paso(String s) { System.out.println("\n== " + s); }
  public static void main(String[] a) {
    UsuarioFactory f = new UsuarioFactory();
    Usuario luis = f.crearUsuario(Rol.RESIDENTE, new DatosUsuario("Luis","luis@cond.pe",10,"301","A","","",false));
    paso("1. Residente creado por la fábrica: " + luis.getClass().getSimpleName() + " (" + luis.getCorreo() + ", condominio " + luis.getCondominioId() + ")");
    SmtpClient smtp = (d, asunto, m) -> System.out.println("   [correo -> " + d + "] " + m);
    RegistroAuditoria aud = new RegistroAuditoria();
    IncidenciaService s = new IncidenciaService(List.of(aud, new NotificationService(List.of(new EmailAdapter(smtp)), luis.getCorreo())));
    IncidenciaRepository repo = new IncidenciaRepository();
    Incidencia t = new Incidencia(1, "UF-0001", 10, new Categoria("Fuga de agua", "Gasfitería"));
    repo.save(t);
    paso("2. Incidencia registrada: " + t.getCodigo() + " | estado: " + t.getEstadoActual().codigo());
    List<Tecnico> tecs = List.of(new Tecnico(1,"Gasfitería",true,2), new Tecnico(2,"Gasfitería",true,1), new Tecnico(3,"Electricidad",true,0));
    paso("3. Técnicos: #1 Gasfitería carga 2 | #2 Gasfitería carga 1 | #3 Electricidad carga 0");
    Tecnico e = new AsignacionMenorCargaStrategy().elegirTecnico(t, tecs).orElseThrow();
    System.out.println("   Strategy elige al técnico #" + e.getId() + " (especialidad requerida y menor carga)");
    s.asignar(t, e, "admin@cond.pe");   System.out.println("   estado: " + t.getEstadoActual().codigo());
    s.iniciar(t, e, "tecnico@cond.pe"); System.out.println("   estado: " + t.getEstadoActual().codigo());
    paso("4. Intento de resolver SIN evidencia (regla RFU-023)");
    try { s.resolver(t, "Se reparó la fuga", "tecnico@cond.pe"); }
    catch (TransicionInvalidaException ex) { System.out.println("   RECHAZADO: " + ex.getMessage()); }
    t.agregarEvidencia("foto-reparacion.jpg");
    paso("5. Resolver CON evidencia y cerrar con calificación 5");
    s.resolver(t, "Se reparó la fuga", "tecnico@cond.pe"); System.out.println("   estado: " + t.getEstadoActual().codigo());
    s.cerrar(t, 5, "luis@cond.pe");                         System.out.println("   estado: " + t.getEstadoActual().codigo());
    paso("6. Auditoría (Observer) - " + aud.getRegistros().size() + " registros");
    aud.getRegistros().forEach(r -> System.out.println("   " + r.evento() + " por " + r.usuario() + " en " + r.entidad() + " #" + r.entidadId()));
    paso("7. Aislamiento por condominio");
    System.out.println("   condominio 10 ve el ticket: " + repo.buscarPorIdYCondominio(1,10).isPresent());
    System.out.println("   condominio 20 ve el ticket: " + repo.buscarPorIdYCondominio(1,20).isPresent());
  }
}
