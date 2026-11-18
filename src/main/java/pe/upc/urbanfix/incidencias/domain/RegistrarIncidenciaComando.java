package pe.upc.urbanfix.incidencias.domain;
public record RegistrarIncidenciaComando(long condominioId, Categoria categoria, String ubicacion,
                                         boolean confirmaDuplicado, String actor) {}
