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
 * Endpoints para publicaciones (timeline, CRUD publicaciones).
 */
@Tag(name = "Publications", description = "Publication operations")
@Validated
@RequestMapping("/api/v1/publications")
public interface PublicationController {

    

    /**
     * Obtener todas las publicaciones (privado).
     */
    @GetMapping("/")
    ResponseEntity<Page<PublicationDto>> getAllPublications(Pageable pageable);

    /**
     * Obtener todas las publicaciones de un usuario (público).
     */
    @GetMapping("/user/{userId}")
    ResponseEntity<Page<PublicationDto>> getPublicationsByUser(@PathVariable Integer userId, Pageable pageable);

    

    /**
     * Obtener publicaciones de los usuarios que sigue un usuario (timeline) (privado).
     */
    @GetMapping("/timeline/{userId}")
    ResponseEntity<Page<PublicationDto>> getTimeline(@PathVariable Integer userId, Pageable pageable);

    /**
     * Insertar publicación (privado).
     */
    @PostMapping("/")
    ResponseEntity<PublicationDto> createPublication(@RequestBody @Valid CreatePublicationDto dto);

    /**
     * Editar publicación (privado).
     */
    @PutMapping("/{publicationId}")
    ResponseEntity<PublicationDto> updatePublication(@PathVariable Integer publicationId,
                                                 @RequestBody @Valid CreatePublicationDto dto);

    /**
     * Borrar publicación (privado).
     */
    @DeleteMapping("/{publicationId}")
    ResponseEntity<Void> deletePublication(@PathVariable Integer publicationId);
}
