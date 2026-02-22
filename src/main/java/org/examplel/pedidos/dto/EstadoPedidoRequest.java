package org.examplel.pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EstadoPedidoRequest {
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|PAGADO|ENVIADO|ENTREGADO", message = "Estado inválido")
    private String estado;
}
