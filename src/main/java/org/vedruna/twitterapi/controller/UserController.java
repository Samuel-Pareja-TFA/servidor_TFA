package org.vedruna.twitterapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.vedruna.twitterapi.controller.dto.CreateUserDto;
import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;
import org.vedruna.twitterapi.controller.dto.UserDto;

/**
 * Endpoints relacionados con usuarios (registro, login, búsquedas y relaciones).
 *
 * Nota: los DTOs referenciados deben crearse en la capa de controller.dto.
 */
@Tag(name = "Users", description = "User operations (register, login, follow lists, etc.)")
@Validated
@RequestMapping("/api/v1/users")
public interface UserController {

    /**
     * Registro público de usuario.
     */
    @PostMapping("/register")
    ResponseEntity<UserDto> registerUser(@RequestBody @Valid CreateUserDto dto);

    /**
     * Login público (devuelve token JWT u objeto TokenDto).
     */
    @PostMapping("/login")
    ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto dto);

    /**
     * Editar solo el username (PATCH privado).
     */
    @PatchMapping("/{userId}/username")
    ResponseEntity<UserDto> updateUsername(@PathVariable Integer userId,
                                           @RequestBody @Valid CreateUserDto partialDto /* puedes crear un DTO específico para username */);

    /**
     * Buscar usuario por username (público).
     */
    @GetMapping("/by-username/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username);

    /**
     * Obtener todos los usuarios que sigue un usuario (privado).
     */
    @GetMapping("/{userId}/following")
    ResponseEntity<Page<UserDto>> getFollowing(@PathVariable Integer userId, Pageable pageable);

    /**
     * Obtener todos los usuarios que siguen a un usuario (privado).
     */
    @GetMapping("/{userId}/followers")
    ResponseEntity<Page<UserDto>> getFollowers(@PathVariable Integer userId, Pageable pageable);
}
