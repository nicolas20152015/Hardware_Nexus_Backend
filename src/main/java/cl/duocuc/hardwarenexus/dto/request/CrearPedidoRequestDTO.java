package cl.duocuc.hardwarenexus.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequestDTO {

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<ItemPedidoRequestDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoRequestDTO {
        @NotNull(message = "El id del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima debe ser 1")
        private Integer cantidad;
    }
}
