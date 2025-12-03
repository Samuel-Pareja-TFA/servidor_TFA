package org.vedruna.twitterapi.service;

/**
 * Servicio de dominio para la funcionalidad de "likes" en publicaciones.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Permitir que un usuario haga like a una publicación.</li>
 *   <li>Permitir que un usuario quite su like de una publicación.</li>
 *   <li>Consultar el número total de likes de una publicación.</li>
 * </ul>
 *
 * <p>La implementación debe:</p>
 * <ul>
 *   <li>Validar la existencia del usuario y la publicación.</li>
 *   <li>Verificar permisos (el usuario autenticado solo puede operar en su propio nombre,
 *       salvo que sea admin).</li>
 *   <li>Evitar duplicados de like (respetando la restricción única en BD).</li>
 * </ul>
 */
public interface LikeService {

    /**
     * Permite que un usuario haga like a una publicación.
     *
     * @param userId        id del usuario que realiza el like
     * @param publicationId id de la publicación a la que se da like
     */
    void likePublication(Integer userId, Integer publicationId);

    /**
     * Permite que un usuario quite su like de una publicación.
     *
     * @param userId        id del usuario que quita el like
     * @param publicationId id de la publicación sobre la que se quita el like
     */
    void unlikePublication(Integer userId, Integer publicationId);

    /**
     * Devuelve el número de likes que tiene una publicación.
     *
     * @param publicationId id de la publicación
     * @return total de likes asociados a la publicación
     */
    long countLikes(Integer publicationId);
}
