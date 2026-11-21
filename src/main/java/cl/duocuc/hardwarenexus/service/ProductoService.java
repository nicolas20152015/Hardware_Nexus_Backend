package cl.duocuc.hardwarenexus.service;

import cl.duocuc.hardwarenexus.dto.request.ProductoRequestDTO;
import cl.duocuc.hardwarenexus.dto.response.CategoriaResponseDTO;
import cl.duocuc.hardwarenexus.dto.response.ProductoResponseDTO;
import cl.duocuc.hardwarenexus.model.Categoria;
import cl.duocuc.hardwarenexus.model.Producto;
import cl.duocuc.hardwarenexus.repository.CategoriaRepository;
import cl.duocuc.hardwarenexus.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos(Long categoriaId, String busqueda) {
        List<Producto> productos;
        if (categoriaId != null) {
            productos = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
        } else if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(busqueda.trim());
        } else {
            productos = productoRepository.findByActivoTrue();
        }

        return productos.stream()
                .map(ProductoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodosParaAdmin() {
        return productoRepository.findAll().stream()
                .map(ProductoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        return ProductoResponseDTO.fromEntity(producto);
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .imagenUrl(request.getImagenUrl())
                .categoria(categoria)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return ProductoResponseDTO.fromEntity(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setCategoria(categoria);
        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }

        return ProductoResponseDTO.fromEntity(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        // Soft delete para mantener integridad referencial con boletas históricas
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
