package org.vedruna.twitterapi.service.impl;

import java.util.Set;
import java.util.HashSet;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.FollowService;
import org.vedruna.twitterapi.service.exception.FollowNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;


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

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void followUser(Integer userId, Integer toFollowId) {
        log.info("Request to follow: userId {} -> toFollowId {}", userId, toFollowId);

        if (userId == null || toFollowId == null) {
            throw new IllegalArgumentException("userId and toFollowId are required");
        }
        if (userId.equals(toFollowId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        UserEntity follower = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserEntity toFollow = userRepository.findById(toFollowId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + toFollowId));

        // autorización: sólo mismo usuario o admin
        checkAuthorization(userId);

        Set<UserEntity> following = follower.getFollowing();
        if (following == null) {
            // Si por alguna razón es null (aunque debería inicializarse), inicializamos
            // (pero normalmente JPA lo gestiona).
            throw new IllegalStateException("Following set is not initialized for user " + userId);
        }

        if (following.contains(toFollow)) {
            log.info("User {} already follows {}", userId, toFollowId);
            return; // operación idempotente
        }

        following.add(toFollow);
        // Guardamos al follower (propietario de la relación many-to-many)
        userRepository.save(follower);
        log.info("User {} now follows {}", userId, toFollowId);
    }

    @Override
    @Transactional
    public void unfollowUser(Integer userId, Integer toUnfollowId) {
        log.info("Request to unfollow: userId {} -> toUnfollowId {}", userId, toUnfollowId);

        if (userId == null || toUnfollowId == null) {
            throw new IllegalArgumentException("userId and toUnfollowId are required");
        }
        if (userId.equals(toUnfollowId)) {
            throw new IllegalArgumentException("User cannot unfollow themselves");
        }

        UserEntity follower = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserEntity toUnfollow = userRepository.findById(toUnfollowId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + toUnfollowId));

        // autorización: sólo mismo usuario o admin
        checkAuthorization(userId);

        Set<UserEntity> following = follower.getFollowing();
        if (following == null || !following.contains(toUnfollow)) {
            throw new FollowNotFoundException("Follow relationship does not exist");
        }

        following.remove(toUnfollow);
        userRepository.save(follower);
        log.info("User {} has unfollowed {}", userId, toUnfollowId);
    }

    /**
     * Comprueba la autorización del principal: solo el propio usuario o ROLE_ADMIN.
     * Lanza AccessDeniedException si no está autorizado.
     */
    private void checkAuthorization(Integer userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Access denied: unauthenticated principal");
        }

        Object principal = auth.getPrincipal();
        Integer authUserId = null;
        boolean isAdmin = false;

        if (principal instanceof UserEntity) {
            UserEntity ue = (UserEntity) principal;
            authUserId = ue.getId();
            isAdmin = ue.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        } else if (principal instanceof UserDetails) {
            UserDetails ud = (UserDetails) principal;
            isAdmin = ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            // authUserId queda null si UserDetails no expone id
        } else {
            throw new AccessDeniedException("Access denied: unauthenticated principal type");
        }

        boolean isSameUser = (authUserId != null && authUserId.equals(userId));
        if (!isSameUser && !isAdmin) {
            throw new AccessDeniedException("Access denied: cannot modify follows for other users");
        }
    }
}

