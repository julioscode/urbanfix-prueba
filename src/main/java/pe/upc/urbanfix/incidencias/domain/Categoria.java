package pe.upc.urbanfix.incidencias.domain;
public record Categoria(String nombre, String especialidadRequerida) {
    public String getEspecialidadRequerida() { return especialidadRequerida; }
}
