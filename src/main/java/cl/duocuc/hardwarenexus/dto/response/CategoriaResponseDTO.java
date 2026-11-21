package cl.duocuc.hardwarenexus.dto.response;

import cl.duocuc.hardwarenexus.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Boolean activo;

    public static CategoriaResponseDTO fromEntity(Categoria c) {
        return CategoriaResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .icono(c.getIcono())
                .activo(c.getActivo())
                .build();
    }
}
