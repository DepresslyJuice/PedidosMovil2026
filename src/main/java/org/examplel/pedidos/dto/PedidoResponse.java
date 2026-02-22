package org.examplel.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private String nombreCliente;
    private String telefonoCliente;
    private String direccion;
    private String detallePedido;
    private String tipoPago;
    private String fotoUrl;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fecha;
    private String estado;
    private Double total;
    private String vendedor;
}
