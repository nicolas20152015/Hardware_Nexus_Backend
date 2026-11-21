package cl.duocuc.hardwarenexus.controller;

import cl.duocuc.hardwarenexus.dto.request.CrearPedidoRequestDTO;
import cl.duocuc.hardwarenexus.dto.response.BoletaResponseDTO;
import cl.duocuc.hardwarenexus.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos y Boletas", description = "Endpoints para creación de compras, consulta de órdenes y boletas electrónicas")
@SecurityRequirement(name = "BearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Crear nuevo pedido / Registrar compra (Cliente o Admin)",
               description = "Procesa el carrito de compras, valida stock, descuenta inventario y emite la boleta con desglose de IVA y total.")
    public ResponseEntity<BoletaResponseDTO> crearPedido(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CrearPedidoRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(userDetails.getUsername(), request));
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar mis pedidos (Cliente autenticado)",
               description = "Retorna el historial completo de boletas y compras del usuario autenticado en la sesión.")
    public ResponseEntity<List<BoletaResponseDTO>> listarMisPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(pedidoService.listarMisPedidos(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Listar todas las órdenes/boletas (ADMIN y VENDEDOR)",
               description = "Conforme a la rúbrica, permite al Administrador y al Vendedor visualizar la lista general de órdenes de la tienda.")
    public ResponseEntity<List<BoletaResponseDTO>> listarTodasLasBoletas() {
        return ResponseEntity.ok(pedidoService.listarTodasLasBoletas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle de una boleta por ID",
               description = "Permite ver el detalle ítem por ítem. Administradores y Vendedores pueden ver cualquier boleta; Clientes solo las suyas.")
    public ResponseEntity<BoletaResponseDTO> obtenerDetalleBoleta(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isAdminOrVendedor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDEDOR"));

        return ResponseEntity.ok(pedidoService.obtenerPorId(id, userDetails.getUsername(), isAdminOrVendedor));
    }
}
