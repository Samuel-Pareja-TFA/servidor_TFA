package org.vedruna.twitterapi.persistance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vedruna.twitterapi.persistance.entity.LikeEntity;

/**
 * Repositorio JPA para gestionar los likes de publicaciones.
 *
 * <p>Proporciona operaciones CRUD básicas y algunos métodos derivados
 * para consultas típicas:</p>
 * <ul>
 *   <li>Comprobar si un usuario ya ha hecho like a una publicación.</li>
 *   <li>Contar cuántos likes tiene una publicación.</li>
 *   <li>Localizar un like concreto (user + publication) para poder borrarlo.</li>
 * </ul>
 */
public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {

    /**
     * Comprueba si existe un like de un usuario sobre una publicación.
     *
     * @param userId        id del usuario
     * @param publicationId id de la publicación
     * @return {@code true} si existe al menos un registro, {@code false} en caso contrario
     */
    boolean existsByUser_IdAndPublication_Id(Integer userId, Integer publicationId);

    /**
     * Cuenta cuántos likes tiene una publicación concreta.
     *
     * @param publicationId id de la publicación
     * @return número de likes asociados
     */
    long countByPublication_Id(Integer publicationId);

    /**
     * Obtiene (si existe) el like hecho por un usuario a una publicación.
     *
     * @param userId        id del usuario
     * @param publicationId id de la publicación
     * @return {@link Optional} con el like si existe, vacío en caso contrario
     */
    Optional<LikeEntity> findByUser_IdAndPublication_Id(Integer userId, Integer publicationId);
}
