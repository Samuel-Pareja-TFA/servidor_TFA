package org.vedruna.twitterapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.vedruna.twitterapi.controller.dto.CreatePublicationDto;
import org.vedruna.twitterapi.controller.dto.PublicationDto;
import org.vedruna.twitterapi.controller.dto.UpdatePublicationDto;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.vedruna.twitterapi.persistance.entity.UserEntity;


/**
 * Interfaz de controlador REST para operaciones de publicaciones.
 *
 * <p>Incluye endpoints para:
 * <ul>
 *   <li>Obtener todas las publicaciones (privado)</li>
 *   <li>Obtener publicaciones de un usuario específico (público)</li>
 *   <li>Obtener timeline de publicaciones de los usuarios que sigue un usuario (privado)</li>
 *   <li>Insertar, actualizar y borrar publicaciones (privado)</li>
 * </ul>
 *
 * <p>Todos los métodos privados requieren autorización mediante JWT y verifican
 * que el usuario autenticado tenga permisos sobre los recursos que intenta modificar.
 *
 * <p>Se utiliza {@link ResponseEntity} para encapsular la respuesta y los códigos HTTP.
 */

@Tag(name = "Publications", description = "Publication operations")
@Validated
@RequestMapping("/api/v1/publications")
public interface PublicationController {

    

    /**
     * Obtener todas las publicaciones (privado).
     *
     * @param pageable Paginación de resultados
     * @return {@link ResponseEntity} con página de {@link PublicationDto}
     */
    @GetMapping("/")
    ResponseEntity<Page<PublicationDto>> getAllPublications(Pageable pageable);

    /**
     * Obtener todas las publicaciones de un usuario específico (público).
     *
     * @param userId ID del usuario
     * @param pageable Paginación de resultados
     * @return {@link ResponseEntity} con página de {@link PublicationDto}
     */
    @GetMapping("/user/{userId}")
    ResponseEntity<Page<PublicationDto>> getPublicationsByUser(@PathVariable Integer userId, Pageable pageable);

    

    /**
     * Obtener timeline de publicaciones de los usuarios que sigue un usuario (privado).
     *
     * @param userId ID del usuario que solicita el timeline
     * @param pageable Paginación de resultados
     * @return {@link ResponseEntity} con página de {@link PublicationDto}
     */
    @GetMapping("/timeline/{userId}")
    ResponseEntity<Page<PublicationDto>> getTimeline(@PathVariable Integer userId, Pageable pageable);

    /**
     * Insertar una nueva publicación (privado).
     *
     * @param dto DTO con los datos de la publicación
     * @return {@link ResponseEntity} con el {@link PublicationDto} creado
     */
    @PostMapping("/")
    ResponseEntity<PublicationDto> createPublication(@RequestBody @Valid CreatePublicationDto dto);

    /**
     * Editar una publicación existente (privado).
     *
     * @param publicationId ID de la publicación a editar
     * @param dto DTO con los datos actualizados
     * @return {@link ResponseEntity} con el {@link PublicationDto} actualizado
     */
    @PutMapping("/{publicationId}")
    ResponseEntity<PublicationDto> updatePublication(@PathVariable Integer publicationId,
                                                 @RequestBody @Valid CreatePublicationDto dto);

    /**
     * Borrar una publicación (privado).
     *
     * @param publicationId ID de la publicación a eliminar
     * @return {@link ResponseEntity} vacío con código HTTP 204 si se eliminó correctamente
     */
    @DeleteMapping("/{publicationId}")
    ResponseEntity<Void> deletePublication(@PathVariable Integer publicationId);
}
