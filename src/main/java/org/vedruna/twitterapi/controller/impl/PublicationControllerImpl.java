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
 * Implementación REST de {@link PublicationController}.
 *
 * <p>Proporciona endpoints para CRUD de publicaciones, incluyendo:
 * <ul>
 *   <li>Obtención de todas las publicaciones (privado)</li>
 *   <li>Obtención de publicaciones de un usuario específico (público)</li>
 *   <li>Timeline de usuarios seguidos (privado)</li>
 *   <li>Creación, actualización y eliminación de publicaciones (privado)</li>
 * </ul>
 * </p>
 *
 * <p>La autorización se realiza para que solo el autor de la publicación o un
 * administrador pueda modificar o eliminar publicaciones ajenas.</p>
 *
 * <p>Se usan los siguientes componentes:
 * <ul>
 *   <li>{@link PublicationService} para la lógica de negocio</li>
 *   <li>{@link PublicationConverter} para convertir entidades a DTOs</li>
 * </ul>
 *
 * <p>Se registra información de cada operación usando {@code log} de Lombok.</p>
 */
@AllArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class PublicationControllerImpl implements PublicationController {

    private final PublicationService publicationService;
    private final PublicationConverter publicationConverter;
    
    
    /**
     * Obtiene todas las publicaciones existentes en el sistema.
     *
     * <p>Este endpoint es privado y requiere que el usuario esté autenticado.
     * Se devuelve una página de publicaciones ({@link Page}) convertidas a DTO ({@link PublicationDto}).</p>
     *
     * <p>El método utiliza {@link PublicationService#getAllPublications(Pageable)}
     * para obtener las entidades de la base de datos y {@link PublicationConverter#toDto(PublicationEntity)}
     * para mapear cada entidad a DTO.</p>
     *
     * <p>Logging: se registra un mensaje informativo indicando que se han solicitado todas las publicaciones.</p>
     *
     * @param pageable objeto de paginación que define la página, tamaño y orden de los resultados
     * @return {@link ResponseEntity} con la página de {@link PublicationDto} y código HTTP 200 OK
     */
    @Override
    public ResponseEntity<Page<PublicationDto>> getAllPublications(Pageable pageable) {
        log.info("Get all publications");
        Page<PublicationEntity> page = publicationService.getAllPublications(pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    /**
     * Obtiene todas las publicaciones de un usuario específico.
     *
     * <p>Este endpoint es público: cualquier usuario puede consultar las publicaciones de otro usuario
     * especificando su {@code userId}.</p>
     *
     * <p>Se utiliza {@link PublicationService#getPublicationsByUser(Integer, Pageable)}
     * para obtener las publicaciones de la base de datos y {@link PublicationConverter#toDto(PublicationEntity)}
     * para convertir cada entidad en un DTO.</p>
     *
     * <p>Logging: se registra el id del usuario cuyas publicaciones se están solicitando.</p>
     *
     * @param userId id del usuario cuyas publicaciones se desean consultar
     * @param pageable objeto de paginación que define la página, tamaño y orden de los resultados
     * @return {@link ResponseEntity} con la página de {@link PublicationDto} y código HTTP 200 OK
     */
    @Override
    public ResponseEntity<Page<PublicationDto>> getPublicationsByUser(Integer userId, Pageable pageable) {
        log.info("Get publications by user {}", userId);
        Page<PublicationEntity> page = publicationService.getPublicationsByUser(userId, pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    /**
     * Obtiene el timeline de un usuario, es decir, todas las publicaciones de los usuarios
     * que sigue el usuario autenticado.
     *
     * <p>Este endpoint es privado: solo el propio usuario o un administrador puede consultar su timeline.
     * Se fuerza el uso del usuario autenticado para garantizar seguridad.</p>
     *
     * <p>Se realiza:
     * <ul>
     *   <li>Obtención del principal autenticado del contexto de seguridad.</li>
     *   <li>Comprobación de roles y permisos (solo el usuario mismo o admin puede acceder).</li>
     *   <li>Obtención de publicaciones mediante {@link PublicationService#getPublicationsOfFollowing(Integer, Pageable)}</li>
     *   <li>Conversión de entidades a DTOs mediante {@link PublicationConverter#toDto(PublicationEntity)}</li>
     * </ul>
     * </p>
     *
     * <p>Logging: se registra el id del usuario cuyo timeline se está consultando.</p>
     *
     * @param userId id del usuario para el que se solicita el timeline (se ignora y se fuerza al usuario autenticado)
     * @param pageable objeto de paginación que define la página, tamaño y orden de los resultados
     * @return {@link ResponseEntity} con la página de {@link PublicationDto} y código HTTP 200 OK
     * @throws AccessDeniedException si el usuario no está autenticado o intenta acceder al timeline de otro usuario
     */
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

    /**
     * Crea una nueva publicación asociada al usuario autenticado.
     *
     * <p>Este endpoint es privado y requiere que el usuario esté autenticado.
     * La fecha de creación se establece automáticamente.</p>
     *
     * <p>Se realiza:
     * <ul>
     *   <li>Obtención del usuario autenticado del contexto de seguridad.</li>
     *   <li>Creación de una {@link PublicationEntity} con el texto recibido y fecha actual.</li>
     *   <li>Guardado de la entidad mediante {@link PublicationService#createPublication(PublicationEntity)}.</li>
     *   <li>Conversión de la entidad a DTO mediante {@link PublicationConverter#toDto(PublicationEntity)}.</li>
     * </ul>
     * </p>
     *
     * <p>Logging: se registra el texto de la publicación que se está creando.</p>
     *
     * @param dto DTO con la información de la publicación a crear
     * @return {@link ResponseEntity} con {@link PublicationDto} de la publicación creada y código HTTP 201 Created
     * @throws AccessDeniedException si el usuario no está autenticado
     */
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

    /**
     * Actualiza una publicación existente.
     *
     * <p>Solo el autor de la publicación o un administrador puede actualizarla.
     * La fecha de actualización se establece automáticamente.</p>
     *
     * <p>Se realiza:
     * <ul>
     *   <li>Obtención del usuario autenticado y roles del contexto de seguridad.</li>
     *   <li>Verificación de permisos: solo autor o admin.</li>
     *   <li>Actualización del texto y fecha de la publicación.</li>
     *   <li>Guardado de los cambios mediante {@link PublicationService#updatePublication(Integer, PublicationEntity)}.</li>
     *   <li>Conversión de la entidad a DTO mediante {@link PublicationConverter#toDto(PublicationEntity)}.</li>
     * </ul>
     * </p>
     *
     * <p>Logging: se registra el id de la publicación y el nuevo texto.</p>
     *
     * @param publicationId id de la publicación a actualizar
     * @param dto DTO con la nueva información de la publicación
     * @return {@link ResponseEntity} con {@link PublicationDto} de la publicación actualizada y código HTTP 200 OK
     * @throws AccessDeniedException si el usuario no está autenticado o no es el autor/admin
     */
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

    /**
     * Elimina una publicación existente.
     *
     * <p>Solo el autor de la publicación o un administrador puede eliminarla.</p>
     *
     * <p>Se realiza:
     * <ul>
     *   <li>Obtención del usuario autenticado y roles del contexto de seguridad.</li>
     *   <li>Verificación de permisos: solo autor o admin.</li>
     *   <li>Eliminación de la publicación mediante {@link PublicationService#deletePublication(Integer)}.</li>
     * </ul>
     * </p>
     *
     * <p>Logging: se registra el id de la publicación eliminada y el id del usuario que realizó la operación.</p>
     *
     * @param publicationId id de la publicación a eliminar
     * @return {@link ResponseEntity} vacío con código HTTP 204 No Content
     * @throws AccessDeniedException si el usuario no está autenticado o no es el autor/admin
     */
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
