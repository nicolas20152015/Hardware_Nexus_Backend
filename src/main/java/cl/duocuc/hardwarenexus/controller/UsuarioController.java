package cl.duocuc.hardwarenexus.controller;

import cl.duocuc.hardwarenexus.dto.response.UsuarioResponseDTO;
import cl.duocuc.hardwarenexus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para consulta de perfil y administración de usuarios")
@SecurityRequirement(name = "BearerAuth")
public class UsuarioController {

    private final AuthService authService;

    @GetMapping("/perfil")
    @Operation(summary = "Consultar mi perfil", description = "Obtiene los datos del usuario actualmente autenticado mediante su token JWT")
    public ResponseEntity<UsuarioResponseDTO> obtenerMiPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.obtenerPerfil(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios (Solo ADMIN)", description = "Retorna el listado completo de usuarios registrados en el sistema")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarTodos());
    }
}
