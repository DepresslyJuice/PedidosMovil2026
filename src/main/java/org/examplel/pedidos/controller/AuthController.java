package org.examplel.pedidos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.dto.AuthResponse;
import org.examplel.pedidos.dto.LoginRequest;
import org.examplel.pedidos.dto.RegisterRequest;
import org.examplel.pedidos.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
