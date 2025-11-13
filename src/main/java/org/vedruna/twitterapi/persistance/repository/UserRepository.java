package org.vedruna.twitterapi.persistance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

/**
 * Repositorio Spring Data JPA para la entidad {@link UserEntity}.
 *
 * <p>Proporciona métodos para las operaciones CRUD básicas (heredadas de
 * {@link JpaRepository}) y define consultas específicas utilizadas por la
 * aplicación, principalmente relacionadas con la búsqueda de usuarios y la
 * recuperación de relaciones de seguimiento (following/followers) en forma
 * paginada.</p>
 *
 * <p>Notas de diseño:</p>
 * <ul>
 *   <li>Usamos {@link Page} y {@link Pageable} para devolver resultados
 *       paginados en endpoints que muestran listados (búsqueda pública,
 *       timeline, seguidores, etc.).</li>
 *   <li>Las consultas {@code @Query} están escritas en JPQL y evitan cargar
 *       colecciones completas en memoria cuando no es necesario.</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    /**
     * Busca un usuario por su nombre de usuario exacto.
     *
     * <p>Consulta JPQL equivalente: {@code SELECT u FROM UserEntity u WHERE
     * u.username = :username}. Se utiliza en procesos de autenticación y en
     * búsquedas donde se necesita recuperar un usuario por su login.</p>
     *
     * @param username nombre de usuario a buscar (no {@code null}).
     * @return la entidad {@link UserEntity} encontrada o {@code null} si no
     *         existe (la interfaz actual devuelve el tipo directamente).
     */
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    UserEntity findByUsername(@Param("username") String username);

    /**
     * Indica si existe un usuario con el username dado.
     *
     * @param username nombre de usuario a comprobar.
     * @return {@code true} si ya existe un usuario con ese username.
     */
    boolean existsByUsername(String username);

    /**
     * Indica si existe un usuario con el email dado.
     *
     * @param email correo electrónico a comprobar.
     * @return {@code true} si ya existe un usuario con ese email.
     */
    boolean existsByEmail(String email);

    /**
     * Búsqueda pública por nombre de usuario parcial (case-insensitive),
     * con soporte de paginación.
     *
     * <p>Spring Data JPA genera la consulta a partir del nombre del método
     * {@code findByUsernameContainingIgnoreCase} y aplica el paginado
     * indicado en {@code pageable}.</p>
     *
     * @param q        texto de búsqueda que debe estar contenido en el
     *                 {@code username} (sin distinguir mayúsculas/minúsculas).
     * @param pageable objeto de paginación y orden.
     * @return página de {@link UserEntity} que coinciden con la búsqueda.
     */
    Page<UserEntity> findByUsernameContainingIgnoreCase(String q, Pageable pageable);

    /**
     * Recupera (paginado) el conjunto de usuarios que el usuario con id
     * {@code userId} está siguiendo.
     *
     * <p>La consulta JPQL {@code SELECT f FROM UserEntity u JOIN u.following f
     * WHERE u.id = :userId} selecciona directamente los usuarios seguidos sin
     * tener que inicializar la colección {@code following} completa en la
     * entidad {@code UserEntity}.</p>
     *
     * @param userId   id del usuario cuyas relaciones de following se consultan.
     * @param pageable paginación y orden para la consulta.
     * @return página de usuarios que sigue el usuario dado.
     */
    @Query("SELECT f FROM UserEntity u JOIN u.following f WHERE u.id = :userId")
    Page<UserEntity> findFollowingByUserId(@Param("userId") Integer userId, Pageable pageable);

    /**
     * Recupera (paginado) los seguidores del usuario con id {@code userId}.
     *
     * <p>Consulta JPQL: {@code SELECT follower FROM UserEntity u JOIN u.followers follower
     * WHERE u.id = :userId}. Útil para mostrar listas de seguidores en la
     * interfaz de usuario o en APIs públicas.</p>
     *
     * @param userId   id del usuario cuyos seguidores se desean obtener.
     * @param pageable paginación y orden para la consulta.
     * @return página de usuarios que siguen al usuario dado.
     */
    @Query("SELECT follower FROM UserEntity u JOIN u.followers follower WHERE u.id = :userId")
    Page<UserEntity> findFollowersByUserId(@Param("userId") Integer userId, Pageable pageable);
}
