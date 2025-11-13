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
    /**
     * Repositorio para consultas sobre publicaciones.
     *
     * <p>Esta interfaz extiende {@link JpaRepository} para proporcionar las
     * operaciones CRUD y además define consultas específicas usadas por la
     * aplicación (paginadas) para obtener publicaciones filtradas por usuario
     * o por conjuntos de usuarios. Todas las consultas devuelven objetos
     * {@link PublicationEntity} envueltos en {@link Page} para soportar
     * paginación en la capa de servicio/controlador.</p>
     */

    /**
     * Devuelve todas las publicaciones de un usuario concreto, paginadas.
     *
     * <p>Firma generada por Spring Data JPA: {@code findAllByUserId} crea la
     * consulta equivalente a {@code SELECT p FROM PublicationEntity p WHERE
     * p.user.id = :userId} con paginación. Usar {@code Pageable} permite
     * controlar página, tamaño y orden.</p>
     *
     * @param userId  id del usuario autor de las publicaciones (no {@code null}).
     * @param pageable objeto de paginación y ordenación (puede contener
     *                 orden por fecha, etc.).
     * @return página de {@link PublicationEntity} que contiene las publicaciones
     *         del usuario especificado.
     */
    Page<PublicationEntity> findAllByUserId(Integer userId, Pageable pageable);

    /**
     * Devuelve todas las publicaciones cuyos autores estén en la lista
     * proporcionada (por ejemplo, los usuarios que sigue un determinado
     * usuario), paginadas.
     *
     * <p>Esta consulta se usa típicamente para construir un timeline donde se
     * muestran publicaciones de varios autores. Spring Data JPA generará la
     * consulta correspondiente basada en el nombre del método.</p>
     *
     * @param users    lista de entidades {@code UserEntity} que actúan como
     *                 autores incluidos en el filtro.
     * @param pageable paginación y orden para la consulta.
     * @return página de publicaciones cuyos autores están en la lista dada.
     */
    Page<PublicationEntity> findAllByUserIn(List<org.vedruna.twitterapi.persistance.entity.UserEntity> users, Pageable pageable);

    /**
     * Obtiene las publicaciones escritas por usuarios que forman parte de la
     * colección {@code following} de un usuario determinado.
     *
     * <p>Esta consulta está implementada con JPQL explícito para evitar cargar
     * en memoria la colección {@code following} del usuario (lo que podría
     * provocar problemas de inicialización perezosa o consumo innecesario de
     * memoria). La subconsulta interna {@code (SELECT f FROM UserEntity u JOIN
     * u.following f WHERE u.id = :userId)} devuelve los usuarios seguidos por
     * el usuario con id {@code :userId}, y la consulta exterior selecciona
     * las publicaciones cuyo autor está en ese conjunto.</p>
     *
     * @param userId   id del usuario cuya lista {@code following} se busca.
     * @param pageable paginación y orden para la consulta.
     * @return página de publicaciones de los usuarios seguidos por {@code userId}.
     */
    @Query("SELECT p FROM PublicationEntity p WHERE p.user IN (SELECT f FROM UserEntity u JOIN u.following f WHERE u.id = :userId)")
    Page<PublicationEntity> findAllByUserInFollowing(@Param("userId") Integer userId, Pageable pageable);

}
