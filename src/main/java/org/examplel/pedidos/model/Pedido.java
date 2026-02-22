package org.examplel.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String direccion;

    @NotBlank
    private String detallePedido;

    @NotBlank
    private String tipoPago;

    private String fotoUrl;

    @NotNull
    private Double latitud;

    @NotNull
    private Double longitud;

    private LocalDateTime fecha;

    @NotBlank
    private String estado;

    @NotNull
    private Double total;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    @ManyToMany
    @JoinTable(
        name = "pedido_productos",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        if (estado == null) {
            estado = "PENDIENTE";
        }
    }
}
