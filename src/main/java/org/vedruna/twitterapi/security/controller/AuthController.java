package org.vedruna.twitterapi.security.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.security.controller.converter.UserConverter;
import org.vedruna.twitterapi.security.controller.dto.*;

import org.vedruna.twitterapi.security.service.AuthService;

/**
 * Controller para auth (login/register/refresh/me).
 * Usamos prefijo /api/v1/auth para integrarlo con tu API actual.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserConverter userConverter;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequestDTO request) {
        UserEntity saved = authService.register(userConverter.registerToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        var token = authService.login(userConverter.loginToEntity(request));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(userConverter.toDto(user));
    }
}
