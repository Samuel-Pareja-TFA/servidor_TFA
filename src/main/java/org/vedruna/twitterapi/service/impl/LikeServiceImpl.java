package org.vedruna.twitterapi.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.LikeEntity;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.LikeRepository;
import org.vedruna.twitterapi.persistance.repository.PublicationRepository;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.LikeService;
import org.vedruna.twitterapi.service.exception.PublicationNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de {@link LikeService} que gestiona los likes de publicaciones.
 *
 * <p>Aplica reglas de negocio como:</p>
 * <ul>
 *   <li>Solo el propio usuario (o un admin) puede hacer/retirar likes en su nombre.</li>
 *   <li>Si un usuario intenta hacer like dos veces a la misma publicación, la operación
 *       se trata como idempotente (no se crea un duplicado).</li>
 *   <li>Validación de existencia de usuario y publicación.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    @Override
    @Transactional
    public void likePublication(Integer userId, Integer publicationId) {
        log.info("Solicitud de like: userId {} -> publicationId {}", userId, publicationId);

        checkAuthorization(userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        PublicationEntity publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id " + publicationId));

        // Si ya existe el like, no hacemos nada (idempotente)
        if (likeRepository.existsByUser_IdAndPublication_Id(userId, publicationId)) {
            log.info("Like ya existente para userId {} en publicationId {}. No se crea duplicado.", userId, publicationId);
            return;
        }

        LikeEntity like = new LikeEntity();
        like.setUser(user);
        like.setPublication(publication);
        like.setCreateDate(LocalDateTime.now());

        likeRepository.save(like);
        log.info("Like creado para userId {} en publicationId {}", userId, publicationId);
    }

    @Override
    @Transactional
    public void unlikePublication(Integer userId, Integer publicationId) {
        log.info("Solicitud de unlike: userId {} -> publicationId {}", userId, publicationId);

        checkAuthorization(userId);

        // Si el like existe, se borra; si no, la operación es idempotente (no se lanza error)
        likeRepository.findByUser_IdAndPublication_Id(userId, publicationId)
                .ifPresent(like -> {
                    likeRepository.delete(like);
                    log.info("Like eliminado para userId {} en publicationId {}", userId, publicationId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikes(Integer publicationId) {
        log.info("Contando likes para publicationId {}", publicationId);

        if (!publicationRepository.existsById(publicationId)) {
            throw new PublicationNotFoundException("Publication not found with id " + publicationId);
        }

        return likeRepository.countByPublication_Id(publicationId);
    }

    /**
     * Verifica que el usuario autenticado es el mismo que {@code userId} o que
     * dispone del rol {@code ROLE_ADMIN}. En caso contrario lanza
     * {@link AccessDeniedException}.
     *
     * @param userId id del usuario sobre el que se desea operar
     */
    private void checkAuthorization(Integer userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth != null ? auth.getPrincipal() : null;

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
        } else {
            throw new AccessDeniedException("Access denied: unauthenticated principal");
        }

        boolean isSameUser = (authUserId != null && authUserId.equals(userId));
        if (!isSameUser && !isAdmin) {
            throw new AccessDeniedException("Access denied: cannot like/unlike on behalf of other user");
        }
    }
}
