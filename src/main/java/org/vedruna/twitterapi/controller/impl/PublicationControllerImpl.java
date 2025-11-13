package org.vedruna.twitterapi.controller.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.twitterapi.controller.PublicationController;
import org.vedruna.twitterapi.controller.converter.PublicationConverter;
import org.vedruna.twitterapi.controller.dto.CreatePublicationDto;
import org.vedruna.twitterapi.controller.dto.PublicationDto;
import org.vedruna.twitterapi.controller.dto.UpdatePublicationDto;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.service.PublicationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación REST del PublicationController.
 */
@AllArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class PublicationControllerImpl implements PublicationController {

    private final PublicationService publicationService;
    private final PublicationConverter publicationConverter;
    
    

    @Override
    public ResponseEntity<Page<PublicationDto>> getAllPublications(Pageable pageable) {
        log.info("Get all publications");
        Page<PublicationEntity> page = publicationService.getAllPublications(pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    @Override
    public ResponseEntity<Page<PublicationDto>> getPublicationsByUser(Integer userId, Pageable pageable) {
        log.info("Get publications by user {}", userId);
        Page<PublicationEntity> page = publicationService.getPublicationsByUser(userId, pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    @Override
    public ResponseEntity<Page<PublicationDto>> getTimeline(Integer userId, Pageable pageable) {
        log.info("Get timeline for userId {}", userId);

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
        } else {
            throw new AccessDeniedException("Access denied: unauthenticated principal");
        }

        boolean isSameUser = (authUserId != null && authUserId.equals(userId));
        if (!isSameUser && !isAdmin) {
            throw new AccessDeniedException("Access denied: cannot view other users' timeline");
        }

        // Forzar que se use solo el usuario autenticado
        userId = authUserId;

        Page<PublicationEntity> page = publicationService.getPublicationsOfFollowing(userId, pageable);

        // Mapear a DTO usando el converter
        Page<PublicationDto> dtoPage = page.map(publicationConverter::toDto);

        return ResponseEntity.ok(dtoPage);
    }


    @Override
    public ResponseEntity<PublicationDto> createPublication(@Valid CreatePublicationDto dto) {
    log.info("Create publication request: {}", dto.getText());

    // 1) Obtener el usuario autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth != null ? auth.getPrincipal() : null;

    UserEntity authUser;
    if (principal instanceof UserEntity) {
        authUser = (UserEntity) principal;
    } else {
        throw new AccessDeniedException("Access denied: unauthenticated principal");
    }

    // 2) Crear entidad de publicación
    PublicationEntity entity = new PublicationEntity();
    entity.setUser(authUser); // asignamos directamente el usuario autenticado
    entity.setText(dto.getText());
    entity.setCreateDate(LocalDateTime.now()); // si tienes fecha de creación

    // 3) Guardar publicación
    PublicationEntity saved = publicationService.createPublication(entity);

    // 4) Convertir a DTO y devolver
    PublicationDto response = publicationConverter.toDto(saved);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PublicationDto> updatePublication(Integer publicationId,
                                                        @Valid @RequestBody CreatePublicationDto dto) {
    log.info("Update publication {} -> {}", publicationId, dto.getText());

    // 1) Obtener principal de seguridad
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
    } else {
        throw new AccessDeniedException("Access denied: unauthenticated principal");
    }

    // 2) Cargar la publicación existente
    PublicationEntity existing = publicationService.getPublicationById(publicationId);

    // 3) Permisos: solo autor o admin
    Integer ownerId = existing.getUser() != null ? existing.getUser().getId() : null;
    boolean isOwner = (authUserId != null && authUserId.equals(ownerId));
    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException("Access denied: cannot edit another user's publication");
    }

    // 4) Aplicar cambios
    existing.setText(dto.getText());
    existing.setUpdateDate(LocalDateTime.now()); // tu entidad usa LocalDateTime según el service.

    // 5) Guardar usando la firma que ya tienes en el service
    PublicationEntity saved = publicationService.updatePublication(publicationId, existing);

    // 6) Devolver DTO
    return ResponseEntity.ok(publicationConverter.toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deletePublication(Integer publicationId) {
    log.info("Delete publication request id {}", publicationId);

    // 1) Obtener principal de seguridad
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
        // authUserId queda null si UserDetails no expone id
    } else {
        throw new AccessDeniedException("Access denied: unauthenticated principal");
    }

    // 2) Cargar la publicación y comprobar existencia
    PublicationEntity existing = publicationService.getPublicationById(publicationId);

    // 3) Comprobar permisos: solo autor o admin
    Integer ownerId = existing.getUser() != null ? existing.getUser().getId() : null;
    boolean isOwner = (authUserId != null && authUserId.equals(ownerId));
    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException("Access denied: cannot delete another user's publication");
    }

    // 4) Borrar
    publicationService.deletePublication(publicationId);
    log.info("Publication {} deleted by user {}", publicationId, authUserId);

    return ResponseEntity.noContent().build();
    }
}
