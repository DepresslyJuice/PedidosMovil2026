package org.examplel.pedidos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.config.VendedorOnly;
import org.examplel.pedidos.dto.ProductoRequest;
import org.examplel.pedidos.model.Producto;
import org.examplel.pedidos.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
    }

    @VendedorOnly
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .descripcion(request.getDescripcion())
                .build();
        return ResponseEntity.ok(productoRepository.save(producto));
    }

    @VendedorOnly
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDescripcion(request.getDescripcion());
        
        return ResponseEntity.ok(productoRepository.save(producto));
    }

    @VendedorOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
