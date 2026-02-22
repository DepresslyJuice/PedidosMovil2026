package org.examplel.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequest {
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El detalle del pedido es obligatorio")
    private String detallePedido;
    
    @NotBlank(message = "El tipo de pago es obligatorio")
    @Pattern(regexp = "efectivo|transferencia", message = "El tipo de pago debe ser efectivo o transferencia")
    private String tipoPago;
    
    private String fotoBase64;
    
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitud;
    
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitud;
    
    @Valid
    private List<ProductoPedidoDto> productos;
    
    @Data
    public static class ProductoPedidoDto {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
    }
}
