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
 * Implementación REST del UserController.
 *
 * Login temporal delegando a AuthService (sin Spring Security).
 */
@AllArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final AuthService authService; // <-- inyectado

    @Override
    public ResponseEntity<UserDto> registerUser(@Valid CreateUserDto dto) {
        log.info("Register request for username {}", dto.getUsername());
        UserEntity e = userConverter.toEntity(dto);
        UserEntity saved = userService.createUser(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(saved));
    }

    @Override
    public ResponseEntity<TokenDto> login(@Valid LoginDto dto) {
        log.info("Login attempt for username {}", dto.getUsername());
        TokenDto token = authService.login(dto);
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<UserDto> updateUsername(Integer userId, @Valid UpdateUsernameDto partialDto) {
    log.info("Update username for userId {} -> {}", userId, partialDto.getUsername());
    UserEntity updated = userService.updateUsername(userId, partialDto.getUsername());
    return ResponseEntity.ok(userConverter.toDto(updated));
    }

    @Override
    public ResponseEntity<UserDto> getUserByUsername(String username) {
        log.info("Get user by username {}", username);
        UserEntity u = userService.getUserByUsername(username);
        return ResponseEntity.ok(userConverter.toDto(u));
    }

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
 * Obtener los followers de un usuario (privado).
 * Solo permite acceder si el usuario autenticado es el mismo userId o tiene ROLE_ADMIN.
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
