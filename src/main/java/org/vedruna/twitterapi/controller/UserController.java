package org.vedruna.twitterapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.vedruna.twitterapi.controller.dto.CreateUserDto;
import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;
import org.vedruna.twitterapi.controller.dto.UpdateUsernameDto;
import org.vedruna.twitterapi.controller.dto.UserDto;



/**
 * Controlador REST que define los endpoints relacionados con usuarios.
 *
 * <p>Incluye operaciones para:
 * <ul>
 *   <li>Registro de usuario (público)</li>
 *   <li>Login de usuario (público)</li>
 *   <li>Edición de nombre de usuario (privado)</li>
 *   <li>Consulta de usuario por username (público)</li>
 *   <li>Gestión de relaciones: following y followers (privado)</li>
 * </ul>
 *
 * <p>Todos los métodos que exponen datos sensibles, como información de usuarios,
 * requieren autorización y deben validar que el usuario autenticado tenga permisos
 * adecuados. La respuesta de cada endpoint se encapsula en un {@link ResponseEntity}
 * con el código HTTP correspondiente.</p>
 *
 * <p>Uso de DTOs:
 * <ul>
 *   <li>Evitan exponer información sensible (como contraseñas)</li>
 *   <li>Validados mediante anotaciones de Hibernate Validation</li>
 * </ul>
 * </p>
 */
@Tag(name = "Users", description = "User operations (register, login, follow lists, etc.)")
@Validated
@RequestMapping("/api/v1/users")
public interface UserController {

    /**
     * Registro público de un nuevo usuario.
     *
     * @param dto DTO con los datos necesarios para registrar un usuario
     * @return {@link ResponseEntity} con el {@link UserDto} creado y HTTP 201
     */
    @Operation(summary = "Registro público de un nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente")
    })
    @PostMapping("/register")
    ResponseEntity<UserDto> registerUser(@RequestBody @Valid CreateUserDto dto);

    /**
     * Login público de usuario.
     *
     * @param dto DTO con las credenciales (username/email + password)
     * @return {@link ResponseEntity} con un {@link TokenDto} que contiene JWT
     */
    @Operation(summary = "Login público de usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso y token devuelto")
    })
    @PostMapping("/login")
    ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto dto);

    /**
     * Editar solo el username de un usuario existente (privado).
     *
     * @param userId ID del usuario que se quiere actualizar
     * @param partialDto DTO con el nuevo username
     * @return {@link ResponseEntity} con el {@link UserDto} actualizado
     */
    @Operation(summary = "Editar solo el username de un usuario existente (privado)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Username actualizado correctamente")
    })
    @PatchMapping("/{userId}/username")
    ResponseEntity<UserDto> updateUsername(@PathVariable Integer userId,
                                           @RequestBody @Valid UpdateUsernameDto partialDto);

    /**
     * Buscar un usuario por su username (público).
     *
     * @param username Nombre de usuario a buscar
     * @return {@link ResponseEntity} con el {@link UserDto} del usuario encontrado
     */
    @Operation(summary = "Buscar un usuario por su username (público)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente")
    })
    @GetMapping("/by-username/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username);

    /**
     * Obtener todos los usuarios que sigue un usuario (privado).
     *
     * @param userId ID del usuario cuya lista de following se desea obtener
     * @param pageable Paginación de resultados
     * @return {@link ResponseEntity} con una página de {@link UserDto}
     */
    @Operation(summary = "Obtener todos los usuarios que sigue un usuario (privado)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    })
    @GetMapping("/{userId}/following")
    ResponseEntity<Page<UserDto>> getFollowing(@PathVariable Integer userId, Pageable pageable);

    /**
     * Obtener todos los usuarios que siguen a un usuario (privado).
     *
     * @param userId ID del usuario cuyos followers se desean obtener
     * @param pageable Paginación de resultados
     * @return {@link ResponseEntity} con una página de {@link UserDto}
     */
    @Operation(summary = "Obtener todos los usuarios que siguen a un usuario (privado)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de followers obtenida correctamente")
    })
    @GetMapping("/{userId}/followers")
    ResponseEntity<Page<UserDto>> getFollowers(@PathVariable Integer userId, Pageable pageable);
}
