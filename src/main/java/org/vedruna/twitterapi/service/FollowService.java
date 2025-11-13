package org.vedruna.twitterapi.service;

/**
 * Servicio (contrato) encargado de las operaciones de seguimiento entre
 * usuarios (follow/unfollow).
 *
 * <p>Responsabilidad: exponer las operaciones necesarias para que un usuario
 * comience a seguir a otro o deje de seguirlo. La implementación concreta
 * debe encargarse de validar las reglas de negocio asociadas (por ejemplo:
 * que un usuario no pueda seguirse a sí mismo, que no se creen duplicados
 * en la relación, que existan ambos usuarios, y lanzar excepciones
 * adecuadas en caso de error) y de persistir los cambios mediante el
 * repositorio correspondiente.</p>
 *
 * <p>Consideraciones de diseño y efectos colaterales:</p>
 * <ul>
 *   <li>Validaciones típicas a aplicar en la implementación:
 *     <ul>
 *       <li>Comprobar que {@code userId} y {@code toFollowId} son distintos.</li>
 *       <li>Comprobar que ambos usuarios existen (lanzar
 *           {@code EntityNotFoundException} o excepción custom si no).</li>
 *       <li>Manejar idempotencia: intentar seguir a un usuario ya seguido
 *           puede devolver éxito sin error o lanzar una excepción según la
 *           política de la aplicación.</li>
 *     </ul>
 *   </li>
 *   <li>Las operaciones deben ejecutarse en una transacción adecuada para
 *       evitar inconsistencias en la tabla intermedia (users_follow_users).</li>
 *   <li>Si existen restricciones de concurrencia (muchas peticiones de
 *       follow/unfollow simultáneas), la implementación puede requerir
 *       mecanismos de bloqueo optimista/pesimista o consultas idempotentes
 *       seguras.</li>
 * </ul>
 */
public interface FollowService {

    /**
     * Hace que el usuario identificado por {@code userId} comience a seguir
     * al usuario identificado por {@code toFollowId}.
     *
     * <p>Entrada: dos identificadores de usuario. La implementación debe:
     * validar la existencia de ambos usuarios, comprobar reglas (por ejemplo,
     * no seguirse a sí mismo) y persistir la relación en la tabla de
     * seguimiento. Puede devolver void y lanzar excepciones en caso de error
     * (por ejemplo, entidad no encontrada o violación de integridad).</p>
     *
     * @param userId     id del usuario que realiza la acción de seguir.
     * @param toFollowId id del usuario que va a ser seguido.
     */
    void followUser(Integer userId, Integer toFollowId);

    /**
     * Hace que el usuario identificado por {@code userId} deje de seguir
     * al usuario identificado por {@code toUnfollowId}.
     *
     * <p>Entrada: dos identificadores de usuario. La implementación debe
     * comprobar existencia y permisos, eliminar la relación de seguimiento
     * (si existe) y comportarse de forma idempotente o lanzar excepciones
     * según la política del proyecto.</p>
     *
     * @param userId       id del usuario que deja de seguir.
     * @param toUnfollowId id del usuario que debe dejar de ser seguido.
     */
    void unfollowUser(Integer userId, Integer toUnfollowId);
}
