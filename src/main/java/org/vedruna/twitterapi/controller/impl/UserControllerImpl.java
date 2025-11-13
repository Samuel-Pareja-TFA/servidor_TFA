package org.vedruna.twitterapi.controller.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.twitterapi.controller.UserController;
import org.vedruna.twitterapi.controller.converter.UserConverter;
import org.vedruna.twitterapi.controller.dto.CreateUserDto;
import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;
import org.vedruna.twitterapi.controller.dto.UserDto;
import org.vedruna.twitterapi.controller.dto.UpdateUsernameDto;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.service.AuthService;
import org.vedruna.twitterapi.service.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación REST de {@link UserController}.
 *
 * <p>Provee los endpoints de usuario, incluyendo registro, login, actualización de username,
 * y recuperación de listas de seguidores y seguidos. Maneja la autorización de forma que:
 * <ul>
 *   <li>Solo el propio usuario o un administrador puede acceder a sus seguidores o seguidos.</li>
 *   <li>El registro y login son públicos.</li>
 * </ul>
 * </p>
 *
 * <p>Usa:
 * <ul>
 *   <li>{@link UserService} para la lógica de negocio relacionada con usuarios.</li>
 *   <li>{@link UserConverter} para convertir entre {@link UserEntity} y {@link UserDto}.</li>
 *   <li>{@link AuthService} para la autenticación y generación de tokens JWT.</li>
 * </ul>
 * </p>
 *
 * <p>Logging: se registran eventos importantes de cada endpoint usando {@code log} de Lombok.</p>
 */
@AllArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final AuthService authService; // <-- inyectado

    /**
     * Registro público de usuario.
     *
     * @param dto DTO con datos del usuario a registrar
     * @return {@link ResponseEntity} con el usuario creado y código HTTP 201 Created
     */
    @Override
    public ResponseEntity<UserDto> registerUser(@Valid CreateUserDto dto) {
        log.info("Register request for username {}", dto.getUsername());
        UserEntity e = userConverter.toEntity(dto);
        UserEntity saved = userService.createUser(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(saved));
    }

    /**
     * Login público de usuario.
     *
     * @param dto DTO con credenciales de usuario
     * @return {@link ResponseEntity} con {@link TokenDto} y código HTTP 200 OK
     */
    @Override
    public ResponseEntity<TokenDto> login(@Valid LoginDto dto) {
        log.info("Login attempt for username {}", dto.getUsername());
        TokenDto token = authService.login(dto);
        return ResponseEntity.ok(token);
    }

    /**
     * Actualiza solo el username de un usuario autenticado.
     *
     * @param userId id del usuario
     * @param partialDto DTO con el nuevo username
     * @return {@link ResponseEntity} con {@link UserDto} actualizado
     */
    @Override
    public ResponseEntity<UserDto> updateUsername(Integer userId, @Valid UpdateUsernameDto partialDto) {
    log.info("Update username for userId {} -> {}", userId, partialDto.getUsername());
    UserEntity updated = userService.updateUsername(userId, partialDto.getUsername());
    return ResponseEntity.ok(userConverter.toDto(updated));
    }

     /**
     * Recupera un usuario por su username.
     *
     * @param username nombre de usuario
     * @return {@link ResponseEntity} con {@link UserDto}
     */
    @Override
    public ResponseEntity<UserDto> getUserByUsername(String username) {
        log.info("Get user by username {}", username);
        UserEntity u = userService.getUserByUsername(username);
        return ResponseEntity.ok(userConverter.toDto(u));
    }

    /**
     * Obtiene los usuarios que sigue un usuario dado.
     *
     * <p>Solo el propio usuario o un administrador puede consultar esta información.
     *
     * @param userId id del usuario
     * @param pageable información de paginación
     * @return {@link ResponseEntity} con {@link Page} de {@link UserDto}
     */
    @Override
    public ResponseEntity<Page<UserDto>> getFollowing(Integer userId, Pageable pageable) {
    log.info("Get following for userId {}", userId);

    // 1) Obtener el principal de seguridad (puede ser UserEntity o UserDetails)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth != null ? auth.getPrincipal() : null;

    // 2) Extraer id del usuario autenticado y roles
    Integer authUserId = null;
    boolean isAdmin = false;

    if (principal instanceof UserEntity) {
        UserEntity ue = (UserEntity) principal;
        authUserId = ue.getId();
        isAdmin = ue.getAuthorities().stream()
                     .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    } else if (principal instanceof UserDetails) {
        // Si tu UserDetails no expone id, no podemos comparar ids.
        // Pero podemos comprobar roles a partir de authorities.
        UserDetails ud = (UserDetails) principal;
        isAdmin = ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        // authUserId queda null (no tenemos id)
    } else {
        // principal puede ser "anonymousUser" u otro tipo: denegar por defecto
        throw new AccessDeniedException("Access denied: unauthenticated principal");
    }

    // 3) Comprobación de permiso: solo el mismo usuario o admin
    boolean isSameUser = (authUserId != null && authUserId.equals(userId));

    if (!isSameUser && !isAdmin) {
        throw new AccessDeniedException("Access denied: cannot view other users' following");
    }

    // 4) Ejecutar la lógica normal si está autorizado
    Page<UserEntity> page = userService.getFollowing(userId, pageable);
    return ResponseEntity.ok(page.map(userConverter::toDto));
    }

    /**
     * Obtiene los seguidores de un usuario dado.
     *
     * <p>Solo el propio usuario o un administrador puede consultar esta información.
     *
     * @param userId id del usuario
     * @param pageable información de paginación
     * @return {@link ResponseEntity} con {@link Page} de {@link UserDto}
     */
    @Override
    public ResponseEntity<Page<UserDto>> getFollowers(Integer userId, Pageable pageable) {
    log.info("Get followers for userId {}", userId);

    // 1) Obtener el principal de seguridad
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth != null ? auth.getPrincipal() : null;

    Integer authUserId = null;
    boolean isAdmin = false;

    if (principal instanceof UserEntity) {
        UserEntity ue = (UserEntity) principal;
        authUserId = ue.getId();
        isAdmin = ue.getAuthorities().stream()
                     .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    } else if (principal instanceof UserDetails) {
        UserDetails ud = (UserDetails) principal;
        isAdmin = ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        // authUserId queda null si no tenemos id en UserDetails
    } else {
        throw new AccessDeniedException("Access denied: unauthenticated principal");
    }

    boolean isSameUser = (authUserId != null && authUserId.equals(userId));
    if (!isSameUser && !isAdmin) {
        throw new AccessDeniedException("Access denied: cannot view other users' followers");
    }

    Page<UserEntity> page = userService.getFollowers(userId, pageable);
    return ResponseEntity.ok(page.map(userConverter::toDto));
    }
}
