package cl.duocuc.hardwarenexus.controller;

import cl.duocuc.hardwarenexus.dto.request.ProductoRequestDTO;
import cl.duocuc.hardwarenexus.dto.response.CategoriaResponseDTO;
import cl.duocuc.hardwarenexus.dto.response.ProductoResponseDTO;
import cl.duocuc.hardwarenexus.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints de catálogo, consulta, detalle y administración de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar catálogo de productos (Público / Vendedor / Cliente)",
               description = "Permite filtrar por id de categoría o término de búsqueda. Retorna solo productos activos.")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String buscar
    ) {
        return ResponseEntity.ok(productoService.listarProductos(categoriaId, buscar));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un producto por ID",
               description = "Retorna el detalle completo de un producto para la tienda o panel.")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar todos los productos incluyendo inactivos (Solo ADMIN)",
               description = "Listado completo de productos para el panel de administración.")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodosAdmin() {
        return ResponseEntity.ok(productoService.listarTodosParaAdmin());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear nuevo producto (Solo ADMIN)",
               description = "Registra un nuevo producto en el catálogo. Exclusivo para administradores.")
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearProducto(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Actualizar producto existente (Solo ADMIN)",
               description = "Modifica los atributos de un producto existente. Exclusivo para administradores.")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request
    ) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Desactivar/Eliminar producto (Solo ADMIN)",
               description = "Realiza un borrado lógico (soft-delete) del producto para preservar la integridad de boletas.")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías de productos",
               description = "Obtiene la lista de categorías activas para navegación o filtros.")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(productoService.listarCategorias());
    }
}
