package cl.duocuc.hardwarenexus.dto.response;

import cl.duocuc.hardwarenexus.model.Boleta;
import cl.duocuc.hardwarenexus.model.DetalleBoleta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletaResponseDTO {
    private Long id;
    private String numeroBoleta;
    private Long usuarioId;
    private String clienteNombre;
    private String clienteEmail;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String estado;
    private List<DetalleBoletaDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleBoletaDTO {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private String imagenUrl;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public static DetalleBoletaDTO fromEntity(DetalleBoleta d) {
            return DetalleBoletaDTO.builder()
                    .id(d.getId())
                    .productoId(d.getProducto().getId())
                    .productoNombre(d.getProducto().getNombre())
                    .imagenUrl(d.getProducto().getImagenUrl())
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(d.getSubtotal())
                    .build();
        }
    }

    public static BoletaResponseDTO fromEntity(Boleta b) {
        return BoletaResponseDTO.builder()
                .id(b.getId())
                .numeroBoleta(b.getNumeroBoleta())
                .usuarioId(b.getUsuario().getId())
                .clienteNombre(b.getUsuario().getNombre() + " " + b.getUsuario().getApellido())
                .clienteEmail(b.getUsuario().getEmail())
                .fechaEmision(b.getFechaEmision())
                .subtotal(b.getSubtotal())
                .iva(b.getIva())
                .total(b.getTotal())
                .estado(b.getEstado())
                .items(b.getDetalles() != null ? b.getDetalles().stream()
                        .map(DetalleBoletaDTO::fromEntity)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }
}
