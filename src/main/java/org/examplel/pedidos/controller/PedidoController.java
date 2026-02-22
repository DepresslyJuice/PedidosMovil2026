package org.examplel.pedidos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.config.VendedorOnly;
import org.examplel.pedidos.dto.EstadoPedidoRequest;
import org.examplel.pedidos.dto.PedidoRequest;
import org.examplel.pedidos.dto.PedidoResponse;
import org.examplel.pedidos.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.crearPedido(request, username));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> obtenerPedidos(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.obtenerPedidos(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedido(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.obtenerPedido(id, username));
    }

    @VendedorOnly
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoPedidoRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request, username));
    }
}
