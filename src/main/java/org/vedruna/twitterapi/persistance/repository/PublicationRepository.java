package org.vedruna.twitterapi.persistance.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*; //este
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer> {

    // Todas las publicaciones de un usuario (paged)
    Page<PublicationEntity> findAllByUserId(Integer userId, Pageable pageable);

    // Todas las publicaciones cuyos autores estén en una lista (ej. usuarios que sigues)
    Page<PublicationEntity> findAllByUserIn(List<org.vedruna.twitterapi.persistance.entity.UserEntity> users, Pageable pageable);

    /**
     * Nuevo método: obtiene las publicaciones cuyos autores están en la colección
     * 'following' del usuario con id = :userId. Así evitamos cargar la colección
     * 'following' como entidad en memoria (evita inicialización de PersistentSet).
     */
    @Query("SELECT p FROM PublicationEntity p WHERE p.user IN (SELECT f FROM UserEntity u JOIN u.following f WHERE u.id = :userId)")
    Page<PublicationEntity> findAllByUserInFollowing(@Param("userId") Integer userId, Pageable pageable);

}
