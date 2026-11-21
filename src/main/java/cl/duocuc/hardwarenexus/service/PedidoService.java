package cl.duocuc.hardwarenexus.service;

import cl.duocuc.hardwarenexus.dto.request.CrearPedidoRequestDTO;
import cl.duocuc.hardwarenexus.dto.response.BoletaResponseDTO;
import cl.duocuc.hardwarenexus.model.Boleta;
import cl.duocuc.hardwarenexus.model.DetalleBoleta;
import cl.duocuc.hardwarenexus.model.Producto;
import cl.duocuc.hardwarenexus.model.Usuario;
import cl.duocuc.hardwarenexus.repository.BoletaRepository;
import cl.duocuc.hardwarenexus.repository.ProductoRepository;
import cl.duocuc.hardwarenexus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public BoletaResponseDTO crearPedido(String clienteEmail, CrearPedidoRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + clienteEmail));

        BigDecimal subtotalAcumulado = BigDecimal.ZERO;
        List<DetalleBoleta> detalles = new ArrayList<>();

        // 1. Validar productos, stock disponible y preparar detalles
        for (CrearPedidoRequestDTO.ItemPedidoRequestDTO itemDto : request.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + itemDto.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' no se encuentra disponible");
            }

            if (producto.getStock() < itemDto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para '" + producto.getNombre() +
                        "'. Disponible: " + producto.getStock() + ", Solicitado: " + itemDto.getCantidad());
            }

            // Descontar inventario / stock
            producto.setStock(producto.getStock() - itemDto.getCantidad());
            productoRepository.save(producto);

            // Subtotal del ítem
            BigDecimal itemSubtotal = producto.getPrecio().multiply(BigDecimal.valueOf(itemDto.getCantidad()));
            subtotalAcumulado = subtotalAcumulado.add(itemSubtotal);

            DetalleBoleta detalle = DetalleBoleta.builder()
                    .producto(producto)
                    .cantidad(itemDto.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(itemSubtotal)
                    .build();

            detalles.add(detalle);
        }

        // 2. Cálculos financieros (Chile: IVA 19% incluido o desglosado; aquí desglosamos 19% del neto o del total)
        // Cálculo estándar: Subtotal (neto), IVA = Subtotal * 0.19, Total = Subtotal + IVA
        BigDecimal iva = subtotalAcumulado.multiply(BigDecimal.valueOf(0.19)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalAcumulado.add(iva).setScale(2, RoundingMode.HALF_UP);

        // 3. Generar número de boleta único (Ej: BOL-20261121-7482)
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomCode = ThreadLocalRandom.current().nextInt(1000, 9999);
        String numeroBoleta = "BOL-" + fechaStr + "-" + randomCode;

        Boleta boleta = Boleta.builder()
                .numeroBoleta(numeroBoleta)
                .usuario(usuario)
                .subtotal(subtotalAcumulado.setScale(2, RoundingMode.HALF_UP))
                .iva(iva)
                .total(total)
                .estado("COMPLETADO")
                .build();

        // Asociar relación bidireccional
        for (DetalleBoleta det : detalles) {
            det.setBoleta(boleta);
        }
        boleta.setDetalles(detalles);

        Boleta guardada = boletaRepository.save(boleta);
        return BoletaResponseDTO.fromEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> listarMisPedidos(String clienteEmail) {
        Usuario usuario = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + clienteEmail));
        return boletaRepository.findByUsuarioIdOrderByFechaEmisionDesc(usuario.getId()).stream()
                .map(BoletaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> listarTodasLasBoletas() {
        return boletaRepository.findAllByOrderByFechaEmisionDesc().stream()
                .map(BoletaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerPorId(Long id, String usuarioEmail, boolean isAdminOrVendedor) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada con ID: " + id));

        // Validación de permisos: Si es cliente común, solo puede ver su propia boleta
        if (!isAdminOrVendedor && !boleta.getUsuario().getEmail().equalsIgnoreCase(usuarioEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("No tiene permisos para ver esta boleta");
        }

        return BoletaResponseDTO.fromEntity(boleta);
    }
}
