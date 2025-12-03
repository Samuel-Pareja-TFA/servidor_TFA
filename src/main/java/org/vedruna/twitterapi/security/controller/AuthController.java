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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller de autenticación para la aplicación.
 *
 * <p>Se encarga de manejar las operaciones de login, registro, refresh de tokens y
 * obtención de información del usuario autenticado.</p>
 *
 * <p>Endpoints:
 * <ul>
 *     <li>POST /register: registra un nuevo usuario</li>
 *     <li>POST /login: autentica al usuario y devuelve tokens</li>
 *     <li>POST /refresh: renueva el access token usando refresh token</li>
 *     <li>GET /me: devuelve información del usuario autenticado</li>
 * </ul>
 * </p>
 *
 * <p>El controller utiliza:
 * <ul>
 *     <li>{@link AuthService} para la lógica de autenticación y tokens</li>
 *     <li>{@link UserConverter} para convertir entre DTOs y entidades</li>
 * </ul>
 * </p>
 *
 * <p>Todos los endpoints públicos están indicados y aquellos que requieren
 * autorización incluyen la anotación Swagger de seguridad {@code bearerAuth}.</p>
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Auth", description = "Operaciones de autenticación: registro, login, refresh token y datos del usuario autenticado")
public class AuthController {

    private final AuthService authService;
    private final UserConverter userConverter;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Recibe un {@link RegisterRequestDTO} con los datos del usuario, lo convierte
     * a entidad, lo persiste y devuelve un {@link UserDTO} con la información pública.</p>
     *
     * @param request DTO con username, password, email y descripción
     * @return {@link ResponseEntity} con código 201 y {@link UserDTO} creado
     */
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un nuevo usuario en el sistema. Devuelve el DTO público del usuario creado (sin contraseña).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ya existe el usuario")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para registrar un nuevo usuario (username, password, email, description)",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequestDTO.class))
        )
        @RequestBody RegisterRequestDTO request) {

        UserEntity saved = authService.register(userConverter.registerToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(saved));
    }

    /**
     * Autentica al usuario y devuelve tokens de acceso y refresh.
     *
     * <p>Recibe un {@link LoginRequestDTO} con username y password. Devuelve un
     * {@link AuthResponseDTO} con access token y refresh token si la autenticación es correcta.</p>
     *
     * @param request DTO con las credenciales de login
     * @return {@link ResponseEntity} con {@link AuthResponseDTO} y código 200
     */
    @Operation(summary = "Login de usuario", description = "Autentica al usuario con username y password. Devuelve access token y refresh token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación correcta", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales de login (username y password)",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
        )
        @RequestBody LoginRequestDTO request) {

        var token = authService.login(userConverter.loginToEntity(request));
        return ResponseEntity.ok(token);
    }

    /**
     * Renueva el access token usando un refresh token válido.
     *
     * <p>Recibe un {@link RefreshRequestDTO} con el refresh token. Devuelve un
     * {@link AuthResponseDTO} con un nuevo access token.</p>
     *
     * @param request DTO con el refresh token
     * @return {@link ResponseEntity} con {@link AuthResponseDTO} y código 200
     */
    @Operation(summary = "Renovar Access Token usando Refresh Token", description = "Intercambia un refresh token válido por un nuevo access token. Endpoint público (se pasa el refresh token en body).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token renovado", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con el refresh token",
            required = true,
            content = @Content(schema = @Schema(implementation = RefreshRequestDTO.class))
        )
        @RequestBody RefreshRequestDTO request) {

        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    
    /**
     * Devuelve la información pública del usuario autenticado.
     *
     * <p>El usuario se identifica mediante el access token enviado en la petición.
     * No incluye la contraseña.</p>
     *
     * @param user usuario autenticado extraído del token
     * @return {@link ResponseEntity} con {@link UserDTO} y código 200
     */
    @Operation(summary = "Obtener usuario autenticado (me)", description = "Devuelve la información pública del usuario identificado por el access token enviado en la petición.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario autenticado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado o token inválido")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(userConverter.toDto(user));
    }
}
