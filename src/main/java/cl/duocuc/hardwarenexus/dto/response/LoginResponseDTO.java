package cl.duocuc.hardwarenexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    @Builder.Default
    private String tipoToken = "Bearer";
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
}
