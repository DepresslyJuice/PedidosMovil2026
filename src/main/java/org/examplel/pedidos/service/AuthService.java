package org.examplel.pedidos.service;

import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.dto.AuthResponse;
import org.examplel.pedidos.dto.LoginRequest;
import org.examplel.pedidos.dto.RegisterRequest;
import org.examplel.pedidos.model.User;
import org.examplel.pedidos.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "CLIENTE")
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}
