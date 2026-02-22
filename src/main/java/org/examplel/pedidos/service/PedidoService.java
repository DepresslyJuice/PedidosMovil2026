package org.examplel.pedidos.service;

import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.dto.EstadoPedidoRequest;
import org.examplel.pedidos.dto.PedidoRequest;
import org.examplel.pedidos.dto.PedidoResponse;
import org.examplel.pedidos.model.Pedido;
import org.examplel.pedidos.model.Producto;
import org.examplel.pedidos.model.User;
import org.examplel.pedidos.repository.PedidoRepository;
import org.examplel.pedidos.repository.ProductoRepository;
import org.examplel.pedidos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;
    private final String uploadDir = "uploads/fotos/";

    public PedidoResponse crearPedido(PedidoRequest request, String username) {
        User cliente = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fotoUrl = null;
        if (request.getFotoBase64() != null && !request.getFotoBase64().isEmpty() && !request.getFotoBase64().equals("null")) {
            fotoUrl = guardarFoto(request.getFotoBase64());
        }

        List<Producto> productos = new ArrayList<>();
        double total = 0.0;
        
        if (request.getProductos() != null && !request.getProductos().isEmpty()) {
            for (PedidoRequest.ProductoPedidoDto prod : request.getProductos()) {
                Producto producto = productoRepository.findById(prod.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                productos.add(producto);
                total += producto.getPrecio() * prod.getCantidad();
            }
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .direccion(request.getDireccion())
                .detallePedido(request.getDetallePedido())
                .tipoPago(request.getTipoPago())
                .fotoUrl(fotoUrl)
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .estado("PENDIENTE")
                .total(total)
                .productos(productos)
                .build();

        pedido = pedidoRepository.save(pedido);
        return mapToResponse(pedido);
    }

    public List<PedidoResponse> obtenerPedidos(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("CLIENTE".equals(user.getRole())) {
            return pedidoRepository.findByClienteUsername(username)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else {
            return pedidoRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
    }

    public PedidoResponse obtenerPedido(Long id, String username) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("CLIENTE".equals(user.getRole()) && !pedido.getCliente().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        
        return mapToResponse(pedido);
    }

    public PedidoResponse actualizarEstado(Long id, EstadoPedidoRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!"VENDEDOR".equals(user.getRole())) {
            throw new RuntimeException("Solo los vendedores pueden cambiar el estado");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        List<String> estadosValidos = List.of("PENDIENTE", "PAGADO", "ENVIADO", "ENTREGADO");
        if (!estadosValidos.contains(request.getEstado())) {
            throw new RuntimeException("Estado inválido");
        }

        pedido.setEstado(request.getEstado());
        pedido = pedidoRepository.save(pedido);
        return mapToResponse(pedido);
    }

    private String guardarFoto(String base64) {
        try {
            String base64Data = base64;
            if (base64.contains(",")) {
                base64Data = base64.split(",")[1];
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageBytes);
            
            return "/fotos/" + fileName;
        } catch (Exception e) {
            return null;
        }
    }

    private PedidoResponse mapToResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .nombreCliente(pedido.getCliente().getUsername())
                .telefonoCliente(pedido.getCliente().getEmail())
                .direccion(pedido.getDireccion())
                .detallePedido(pedido.getDetallePedido())
                .tipoPago(pedido.getTipoPago())
                .fotoUrl(pedido.getFotoUrl())
                .latitud(pedido.getLatitud())
                .longitud(pedido.getLongitud())
                .fecha(pedido.getFecha())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .vendedor("")
                .build();
    }
}
