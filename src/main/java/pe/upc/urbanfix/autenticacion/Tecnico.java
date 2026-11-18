package pe.upc.urbanfix.autenticacion;
public class Tecnico extends Usuario {
    private final long id; private final String especialidad; private boolean disponible; private int cargaActiva;
    public Tecnico(String especialidad, boolean disponible) { this(0, especialidad, disponible, 0); }
    public Tecnico(long id, String especialidad, boolean disponible, int carga) { this.id = id; this.especialidad = especialidad; this.disponible = disponible; this.cargaActiva = carga; }
    public long getId() { return id; } public String getEspecialidad() { return especialidad; }
    public boolean isDisponible() { return disponible; } public int getCargaActiva() { return cargaActiva; }
    public void incrementarCarga() { cargaActiva++; }
}
