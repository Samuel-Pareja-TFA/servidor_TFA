package org.vedruna.twitterapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

/**
 * Contrato (interfaz) que define las operaciones de negocio relacionadas con
 * la gestión de usuarios en la aplicación.
 *
 * <p>Responsabilidad: exponer métodos para crear y recuperar usuarios,
 * actualizar información básica y consultar relaciones de red (following/
 * followers) con soporte de paginación. La implementación concreta debe
 * encargarse de validar datos, gestionar transacciones y lanzar las
 * excepciones apropiadas (por ejemplo, {@code EntityNotFoundException} cuando
 * corresponda).</p>
 *
 * <p>Contrato de diseño (reglas implícitas esperadas):</p>
 * <ul>
 *   <li>Los métodos que devuelven {@link UserEntity} lanzan
 *       {@code jakarta.persistence.EntityNotFoundException} si el recurso no
 *       existe (esto debe documentarse en la implementación y propagarse
 *       según las políticas del servicio/rest API).</li>
 *   <li>La validación de campos (por ejemplo, unicidad de username/email,
 *       formato de email, longitud de campos) se realizará en la capa de
 *       servicio antes de persistir y se comunicará mediante excepciones
 *       específicas o códigos HTTP adecuados en la capa de controlador.</li>
 *   <li>Los métodos paginados usan {@link Pageable} para permitir controlar
 *       página, tamaño y orden; siempre devuelven una {@link Page}.</li>
 * </ul>
 */
public interface UserService {

    /**
     * Crea (registra) un nuevo usuario en el sistema.
     *
     * <p>Entrada: el objeto {@code user} debe contener los datos mínimos
     * necesarios (username, password hash, email, etc.). La contraseña debe
     * llegar ya cifrada/hasheada cuando la política de la app lo requiera.
     * La implementación debe validar unicidad y otros constraints antes de
     * persistir.</p>
     *
     * @param user entidad {@link UserEntity} con los datos del nuevo usuario.
     * @return la entidad {@link UserEntity} persistida, con el id generado y
     *         otros campos calculados (por ejemplo, fecha de creación).
     * @throws IllegalArgumentException si faltan campos obligatorios o si
     *         violan reglas de validación.
     */
    UserEntity createUser(UserEntity user);

    /**
     * Recupera un usuario por su identificador único.
     *
     * @param userId identificador del usuario.
     * @return la entidad {@link UserEntity} encontrada.
     * @throws jakarta.persistence.EntityNotFoundException si no existe un
     *         usuario con el id proporcionado.
     */
    UserEntity getUserById(Integer userId);

    /**
     * Recupera un usuario por su nombre de usuario (username).
     *
     * @param username nombre de usuario a buscar.
     * @return la entidad {@link UserEntity} correspondiente al username.
     * @throws jakarta.persistence.EntityNotFoundException si no existe el
     *         usuario con ese nombre.
     */
    UserEntity getUserByUsername(String username);

    /**
     * Actualiza únicamente el nombre de usuario (username) de un usuario
     * existente.
     *
     * <p>Esta operación debe validar que el nuevo username cumpla las
     * restricciones (por ejemplo unicidad y longitud) antes de aplicar el
     * cambio. La implementación debe gestionar la transacción y devolver la
     * entidad actualizada.</p>
     *
     * @param userId      id del usuario a modificar.
     * @param newUsername nuevo nombre de usuario deseado.
     * @return la entidad {@link UserEntity} actualizada.
     * @throws jakarta.persistence.EntityNotFoundException si no existe el
     *         usuario con el id indicado.
     * @throws IllegalArgumentException si el nuevo username no cumple la
     *         validación o ya está en uso.
     */
    UserEntity updateUsername(Integer userId, String newUsername);

    /**
     * Obtiene la lista paginada de usuarios que el usuario identificado por
     * {@code userId} sigue (following).
     *
     * <p>Salida: {@link Page} de {@link UserEntity}. El orden y tamaño se
     * controlan mediante {@code pageable}. La implementación normalmente usa
     * una consulta con {@code JOIN} para evitar inicializar colecciones en
     * memoria.</p>
     *
     * @param userId   id del usuario cuyo following se desea consultar.
     * @param pageable objeto de paginación y orden.
     * @return página con los usuarios seguidos por {@code userId}.
     */
    Page<UserEntity> getFollowing(Integer userId, Pageable pageable);

    /**
     * Obtiene la lista paginada de seguidores (followers) del usuario dado.
     *
     * <p>Salida: {@link Page} de {@link UserEntity}. El comportamiento y
     * consideraciones de paginación son análogos al método {@link
     * #getFollowing(Integer, Pageable)}.</p>
     *
     * @param userId   id del usuario cuyos seguidores se desean obtener.
     * @param pageable objeto de paginación y orden.
     * @return página con los usuarios que siguen a {@code userId}.
     */
    Page<UserEntity> getFollowers(Integer userId, Pageable pageable);
}
