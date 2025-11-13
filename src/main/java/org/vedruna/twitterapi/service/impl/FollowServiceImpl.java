package org.vedruna.twitterapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.FollowService;
import org.vedruna.twitterapi.service.exception.FollowNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementación del servicio de seguimiento (follow/unfollow) entre usuarios.
 *
 * <p>Esta clase opera directamente sobre las colecciones {@code following} y {@code followers}
 * definidas en {@link UserEntity}. Proporciona operaciones idempotentes de seguimiento y
 * separación, validando la existencia de los usuarios involucrados y manteniendo la
 * integridad de la relación ManyToMany mediante persistencia del propietario de la relación.
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>El propietario de la relación es la entidad que define la tabla join en la entidad JPA
 *       (según la configuración actual en {@code UserEntity}). Por eso las modificaciones se
 *       hacen sobre la colección {@code following} del usuario que inicia la acción y se persiste
 *       esa entidad.</li>
 *   <li>Las operaciones están anotadas con {@code @Transactional} para asegurar consistencia
 *       en operaciones concurrentes y en cascada de persistencia.</li>
 *   <li>Los métodos lanzan excepciones de dominio claras para que la capa superior (controladores)
 *       pueda mapearlas a respuestas HTTP adecuadas.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    /** Repositorio de usuarios, usado para validar existencia y resolver entidades relacionadas. */
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void followUser(Integer userId, Integer toFollowId) {
        /**
         * Inicia la relación de seguimiento de {@code userId} hacia {@code toFollowId}.
         *
         * <p>Comportamiento:
         * <ul>
         *   <li>No permite que un usuario se siga a sí mismo; en ese caso lanza
         *       {@link FollowNotFoundException} con un mensaje claro.</li>
         *   <li>Verifica existencia de ambos usuarios; si alguno no existe lanza
         *       {@link UserNotFoundException}.</li>
         *   <li>La operación es idempotente: si ya existe la relación no hace persistencia
         *       adicional y simplemente registra la operación en el log.</li>
         * </ul>
         *
         * @param userId id del usuario que inicia el follow.
         * @param toFollowId id del usuario al que se desea seguir.
         * @throws FollowNotFoundException si {@code userId.equals(toFollowId)} (no permitido seguirse a sí mismo) o
         *         si por alguna razón la relación no puede crearse (consistencia).</li>
         * @throws UserNotFoundException si alguno de los usuarios no existe.
         */
        log.info("User {} intenta seguir a {}", userId, toFollowId);
        if (userId.equals(toFollowId)) {
            throw new FollowNotFoundException("Cannot follow yourself");
        }

        UserEntity me = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserEntity other = userRepository.findById(toFollowId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + toFollowId));

        Set<UserEntity> following = me.getFollowing() == null ? new HashSet<>() : me.getFollowing();
        if (!following.contains(other)) {
            following.add(other);
            me.setFollowing(following);
            // persistimos el cambio (dueño de relación es la tabla join definida en UserEntity)
            userRepository.save(me);
            log.info("User {} ahora sigue a {}", userId, toFollowId);
        } else {
            log.info("User {} ya seguía a {}", userId, toFollowId);
        }
    }

    @Override
    @Transactional
    public void unfollowUser(Integer userId, Integer toUnfollowId) {
    /**
     * Elimina la relación de seguimiento de {@code userId} hacia {@code toUnfollowId}.
     *
     * <p>Comportamiento:
     * <ul>
     *   <li>Valida existencia de ambos usuarios; si alguno no existe lanza
     *       {@link UserNotFoundException}.</li>
     *   <li>Si la relación de seguimiento no existe lanza {@link FollowNotFoundException}.</li>
     *   <li>Si existe, la elimina de la colección {@code following} y persiste el cambio.</li>
     * </ul>
     *
     * @param userId id del usuario que deja de seguir.
     * @param toUnfollowId id del usuario que deja de ser seguido.
     * @throws UserNotFoundException si alguno de los usuarios no existe.
     * @throws FollowNotFoundException si no existía la relación de seguimiento.
     */
    log.info("User {} intenta dejar de seguir a {}", userId, toUnfollowId);
    UserEntity me = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
    UserEntity other = userRepository.findById(toUnfollowId)
        .orElseThrow(() -> new UserNotFoundException("User not found with id " + toUnfollowId));

    Set<UserEntity> following = me.getFollowing();
    if (following == null || !following.remove(other)) {
        throw new FollowNotFoundException("Follow relationship not found between " + userId + " and " + toUnfollowId);
    }
    me.setFollowing(following);
    userRepository.save(me);
    log.info("User {} dejó de seguir a {}", userId, toUnfollowId);
    }
}

