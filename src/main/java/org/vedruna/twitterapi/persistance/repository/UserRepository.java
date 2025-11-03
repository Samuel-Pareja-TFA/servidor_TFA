package org.vedruna.twitterapi.persistance.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // Buscar por username (para login / búsqueda pública)
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    UserEntity findByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Búsqueda pública por username (contains) con paginación
    Page<UserEntity> findByUsernameContainingIgnoreCase(String q, Pageable pageable);

    // Obtener usuarios que sigue un usuario (pageable)
    @Query("SELECT f FROM UserEntity u JOIN u.following f WHERE u.id = :userId")
    Page<UserEntity> findFollowingByUserId(@Param("userId") Integer userId, Pageable pageable);

    // Obtener seguidores de un usuario (pageable)
    @Query("SELECT follower FROM UserEntity u JOIN u.followers follower WHERE u.id = :userId")
    Page<UserEntity> findFollowersByUserId(@Param("userId") Integer userId, Pageable pageable);
}
