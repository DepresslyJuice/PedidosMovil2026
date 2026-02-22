package org.examplel.pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    private String descripcion;
}
