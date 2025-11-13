package org.vedruna.twitterapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;

/**
 * Contrato (interfaz) que define las operaciones de negocio para gestionar
 * publicaciones en la aplicación.
 *
 * <p>Responsabilidad: exponer métodos para listar, crear, actualizar y
 * eliminar publicaciones, así como para obtener publicaciones filtradas por
 * usuario o por la red de seguimiento (following). Las implementaciones de
 * esta interfaz deben encargarse de validar entradas, gestionar
 * transacciones, persistir entidades y lanzar las excepciones apropiadas
 * cuando proceda.</p>
 *
 * <p>Notas generales:</p>
 * <ul>
 *   <li>Los métodos que devuelven {@link Page} usan {@link Pageable} para
 *       controlar paginación y ordenación desde la capa de servicio o
 *       controlador (API REST).</li>
 *   <li>Las operaciones de creación/actualización deben validar el
 *       contenido (por ejemplo, que el texto no esté vacío) y asignar
 *       timestamps (createDate/updateDate) si la implementación no lo hace
 *       automáticamente mediante listeners JPA.</li>
 *   <li>Las implementaciones deben manejar correctamente errores como
 *       {@code EntityNotFoundException} cuando se intente actualizar o
 *       eliminar una publicación inexistente.</li>
 * </ul>
 */
public interface PublicationService {

    /**
     * Recupera todas las publicaciones en forma paginada.
     *
     * @param pageable objeto que controla página, tamaño y orden.
     * @return página de {@link PublicationEntity} con todas las publicaciones
     *         (según los parámetros de paginación y orden).
     */
    Page<PublicationEntity> getAllPublications(Pageable pageable);

    /**
     * Recupera las publicaciones creadas por un usuario concreto (paginadas).
     *
     * @param userId   id del usuario autor.
     * @param pageable paginación y orden.
     * @return página de publicaciones del usuario indicado.
     */
    Page<PublicationEntity> getPublicationsByUser(Integer userId, Pageable pageable);

    /**
     * Obtiene una publicación por su identificador.
     *
     * <p>Uso típico: mostrar detalles de una publicación o prepararla para
     * edición. La implementación debe lanzar {@code EntityNotFoundException}
     * si no existe la publicación con el id proporcionado.</p>
     *
     * @param publicationId id de la publicación.
     * @return la entidad {@link PublicationEntity} correspondiente.
     */
    PublicationEntity getPublicationById(Integer publicationId);

    /**
     * Recupera las publicaciones escritas por los usuarios que sigue el
     * usuario identificado por {@code userId} (paginadas).
     *
     * <p>Útil para construir el timeline de un usuario. La implementación
     * normalmente usará una consulta que seleccione publicaciones donde el
     * autor esté en la colección 'following' del usuario dado para evitar
     * cargar colecciones completas en memoria.</p>
     *
     * @param userId   id del usuario cuyo timeline se desea obtener.
     * @param pageable paginación y orden.
     * @return página de publicaciones de los usuarios seguidos por {@code userId}.
     */
    Page<PublicationEntity> getPublicationsOfFollowing(Integer userId, Pageable pageable);

    /**
     * Crea (persiste) una nueva publicación.
     *
     * <p>La entidad {@code publication} debe contener al menos el autor
     * (usuario) y el texto. La implementación asignará createDate y otros
     * campos necesarios y validará restricciones (por ejemplo texto no vacío).
     *
     * @param publication entidad a persistir.
     * @return la entidad persistida con id y timestamps asignados.
     */
    PublicationEntity createPublication(PublicationEntity publication);

    /**
     * Actualiza una publicación existente.
     *
     * <p>Se identifica la publicación por {@code publicationId} y se aplican
     * los cambios contenidos en {@code publication}. La implementación debe
     * validar permisos (quién puede editar), comprobar existencia y actualizar
     * el {@code updateDate}.</p>
     *
     * @param publicationId id de la publicación a actualizar.
     * @param publication   entidad con los cambios a aplicar.
     * @return la entidad actualizada.
     */
    PublicationEntity updatePublication(Integer publicationId, PublicationEntity publication);

    /**
     * Elimina una publicación por su id.
     *
     * <p>La implementación debe validar permisos (por ejemplo, que el usuario
     * que solicita la eliminación sea el autor o tenga privilegios) y lanzar
     * {@code EntityNotFoundException} si la publicación no existe.</p>
     *
     * @param publicationId id de la publicación a eliminar.
     */
    void deletePublication(Integer publicationId);
}
