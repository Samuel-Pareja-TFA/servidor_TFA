package org.vedruna.twitterapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vedruna.twitterapi.service.LikeService;

/**
 * Controlador REST para gestionar likes en publicaciones.
 *
 * <p>Permite:</p>
 * <ul>
 *   <li>Hacer like a una publicación (privado).</li>
 *   <li>Quitar like de una publicación (privado).</li>
 *   <li>Consultar el número de likes de una publicación (público o privado, según configuración de seguridad).</li>
 * </ul>
 *
 * <p>Las operaciones de like/unlike requieren que el usuario autenticado coincida
 * con {@code userId} o que tenga rol de administrador.</p>
 */
@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * Hacer like a una publicación.
     *
     * @param publicationId id de la publicación a la que se da like
     * @param userId        id del usuario que realiza el like
     * @return {@link ResponseEntity} con mensaje de confirmación
     */
    @PostMapping("/{publicationId}/user/{userId}")
    public ResponseEntity<String> likePublication(
            @PathVariable Integer publicationId,
            @PathVariable Integer userId) {

        likeService.likePublication(userId, publicationId);
        return ResponseEntity.ok("User " + userId + " liked publication " + publicationId);
    }

    /**
     * Quitar like de una publicación.
     *
     * @param publicationId id de la publicación sobre la que se quita el like
     * @param userId        id del usuario que retira el like
     * @return {@link ResponseEntity} con mensaje de confirmación
     */
    @DeleteMapping("/{publicationId}/user/{userId}")
    public ResponseEntity<String> unlikePublication(
            @PathVariable Integer publicationId,
            @PathVariable Integer userId) {

        likeService.unlikePublication(userId, publicationId);
        return ResponseEntity.ok("User " + userId + " unliked publication " + publicationId);
    }

    /**
     * Obtener el número total de likes de una publicación.
     *
     * @param publicationId id de la publicación
     * @return {@link ResponseEntity} con el número de likes
     */
    @GetMapping("/{publicationId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Integer publicationId) {
        long count = likeService.countLikes(publicationId);
        return ResponseEntity.ok(count);
    }
}
